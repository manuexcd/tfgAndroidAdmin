package spring.es.admintfg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.adapter.OrderLinesAdapter;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.OrderLineDTO;
import spring.es.admintfg.dto.UserDTO;

public class OrderDetailsActivity extends AppCompatActivity {
    private static ArrayList<OrderLineDTO> orderLinesArray;
    private OrderLinesAdapter mAdapter;
    private TextView orderDetailId;
    private TextView orderDetailDate;
    private TextView orderDetailPrice;
    private TextView orderDetailUser;
    private Button btnOrderInProgress;
    private Button btnOrderInDelivery;
    private Button btnOrderBuy;
    private Button btnOrderCancel;
    private OrderDTO order;
    private UserDTO user;
    private MyApplication app;

    public void getOrderDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + getIntent().getStringExtra(Constants.ORDER_ID);
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
                order = gson.fromJson(new String(responseBody), OrderDTO.class);
                orderDetailId.setText(String.valueOf(order.getId()));
                orderDetailDate.setText(DateFormat.getDateInstance().format(order.getDate()));
                orderDetailPrice.setText(String.valueOf(new DecimalFormat("#.##").format(order.getTotalPrice())).concat(Constants.EURO));
                orderLinesArray = new ArrayList<>(order.getOrderLines());
                getUserDetails(order.getUserId());

                if (!app.isAdmin()) {
                    btnOrderInProgress.setVisibility(View.INVISIBLE);
                    btnOrderInDelivery.setVisibility(View.INVISIBLE);
                    btnOrderBuy.setVisibility(View.INVISIBLE);
                }

                switch (order.getOrderStatus()) {
                    case Constants.ORDER_STATUS_TEMPORAL:
                        btnOrderInProgress.setVisibility(View.INVISIBLE);
                        btnOrderInDelivery.setVisibility(View.INVISIBLE);
                        btnOrderCancel.setVisibility(View.INVISIBLE);
                        if (!app.isAdmin())
                            btnOrderBuy.setVisibility(View.VISIBLE);
                        break;
                    case Constants.ORDER_STATUS_RECEIVED:
                        if (app.isAdmin()) {
                            btnOrderInProgress.setVisibility(View.VISIBLE);
                            btnOrderInDelivery.setVisibility(View.INVISIBLE);
                            btnOrderBuy.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case Constants.ORDER_STATUS_IN_PROGRESS:
                        btnOrderCancel.setVisibility(View.INVISIBLE);
                        if (app.isAdmin()) {
                            btnOrderInProgress.setVisibility(View.INVISIBLE);
                            btnOrderInDelivery.setVisibility(View.VISIBLE);
                            btnOrderBuy.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case Constants.ORDER_STATUS_IN_DELIVERY:
                    case Constants.ORDER_STATUS_CANCELLED:
                        btnOrderCancel.setVisibility(View.INVISIBLE);
                        btnOrderInProgress.setVisibility(View.INVISIBLE);
                        btnOrderInDelivery.setVisibility(View.INVISIBLE);
                        btnOrderBuy.setVisibility(View.INVISIBLE);
                        break;
                }

                mAdapter.setOrder(order);
                mAdapter.setOrderLines(orderLinesArray);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(OrderDetailsActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void confirmOrder() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PATH_TEMPORAL;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        StringEntity stringOrder = new StringEntity(gson.toJson(order, OrderDTO.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringOrder, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringOrderConfirmed), Toast.LENGTH_LONG).show();
                startActivity(new Intent(OrderDetailsActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(OrderDetailsActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void updateOrder() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        StringEntity stringOrder = new StringEntity(gson.toJson(order, OrderDTO.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringOrder, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringOrderStatusUpdated), Toast.LENGTH_LONG).show();
                startActivity(new Intent(OrderDetailsActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(OrderDetailsActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void cancelOrder(Long orderId) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + "cancel/" + orderId;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());

        client.put(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringConfirmOrderCancelled), Toast.LENGTH_LONG).show();
                startActivity(new Intent(OrderDetailsActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(OrderDetailsActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void getUserDetails(long userId) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + userId;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                user = gson.fromJson(new String(responseBody), UserDTO.class);
                orderDetailUser.setText(user.getName().concat(" ").concat(user.getSurname()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if (statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(OrderDetailsActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        app = (MyApplication) this.getApplication();

        Toolbar orderDetailToolbar = findViewById(R.id.orderDetailsToolbar);
        orderDetailToolbar.setTitle(getResources().getString(R.string.stringOrderDetails));
        orderDetailToolbar.setTitleMarginStart(150);
        orderDetailToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton productDetailBackBtn = findViewById(R.id.orderDetailBackBtn);
        productDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        orderDetailId = findViewById(R.id.orderDetailId);
        orderDetailDate = findViewById(R.id.orderDetailDate);
        orderDetailPrice = findViewById(R.id.orderDetailPrice);
        orderDetailUser = findViewById(R.id.orderDetailUser);

        RecyclerView orderLinesRecyclerView = findViewById(R.id.orderLinesRecyclerView);
        orderLinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new OrderLinesAdapter(orderLinesArray, this);
        orderLinesRecyclerView.setAdapter(mAdapter);

        getOrderDetails();

        btnOrderInProgress = findViewById(R.id.btnOrderInProgress);
        btnOrderInDelivery = findViewById(R.id.btnOrderInDelivery);
        btnOrderBuy = findViewById(R.id.btnOrderBuy);
        btnOrderCancel = findViewById(R.id.btnOrderCancel);

        btnOrderBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setOrderStatus(Constants.ORDER_STATUS_RECEIVED);
                confirmOrder();
            }
        });

        btnOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(order.getId());
            }
        });

        btnOrderInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setOrderStatus(Constants.ORDER_STATUS_IN_PROGRESS);
                updateOrder();
            }
        });

        btnOrderInDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setOrderStatus(Constants.ORDER_STATUS_IN_DELIVERY);
                updateOrder();
            }
        });
    }
}
