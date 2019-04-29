package spring.es.admintfg.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.adapter.OrderLinesAdapter;
import spring.es.admintfg.model.Order;
import spring.es.admintfg.model.OrderLine;

public class OrderDetailsActivity extends AppCompatActivity {
    private static ArrayList<OrderLine> orderLinesArray;
    private OrderLinesAdapter mAdapter;
    private TextView orderDetailId;
    private TextView orderDetailDate;
    private TextView orderDetailUser;
    private TextView orderDetailPrice;
    private Button btnOrderInProgress;
    private Button btnOrderInDelivery;
    private Button btnOrderBuy;
    private Order order;

    public void getOrderDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + getIntent().getStringExtra(Constants.ORDER_ID);
        client.addHeader(Constants.HEADER_AUTHORIZATION, getIntent().getStringExtra(Constants.TOKEN));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
                order = gson.fromJson(new String(responseBody), Order.class);
                orderDetailId.setText(String.valueOf(order.getId()));
                orderDetailDate.setText(String.valueOf(order.getDate()));
                orderDetailUser.setText(order.getUser().getName().concat(" ").concat(order.getUser().getSurname()));
                orderDetailPrice.setText(String.valueOf(new DecimalFormat("#.##").format(order.getTotalPrice())).concat(" ").concat(Constants.EURO));
                orderLinesArray = new ArrayList<>(order.getOrderLines());
                switch (order.getOrderStatus()) {
                    case Constants.ORDER_STATUS_TEMPORAL:
                        btnOrderInProgress.setVisibility(View.INVISIBLE);
                        btnOrderInDelivery.setVisibility(View.INVISIBLE);
                        btnOrderBuy.setVisibility(View.VISIBLE);
                        break;
                    case Constants.ORDER_STATUS_RECEIVED:
                        btnOrderInProgress.setVisibility(View.VISIBLE);
                        btnOrderInDelivery.setVisibility(View.INVISIBLE);
                        btnOrderBuy.setVisibility(View.INVISIBLE);
                        break;
                    case Constants.ORDER_STATUS_IN_PROGRESS:
                        btnOrderInProgress.setVisibility(View.INVISIBLE);
                        btnOrderInDelivery.setVisibility(View.VISIBLE);
                        btnOrderBuy.setVisibility(View.INVISIBLE);
                        break;
                }

                mAdapter.setOrderLines(orderLinesArray);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void confirmOrder() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getIntent().getStringExtra(Constants.TOKEN));

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        StringEntity stringOrder = new StringEntity(gson.toJson(order, Order.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringOrder, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringProductAdded), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderDetailId = findViewById(R.id.orderDetailId);
        orderDetailDate = findViewById(R.id.orderDetailDate);
        orderDetailUser = findViewById(R.id.orderDetailUser);
        orderDetailPrice = findViewById(R.id.orderDetailPrice);

        RecyclerView orderLinesRecyclerView = findViewById(R.id.orderLinesRecyclerView);
        orderLinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new OrderLinesAdapter(orderLinesArray, this);
        orderLinesRecyclerView.setAdapter(mAdapter);

        getOrderDetails();

        btnOrderInProgress = findViewById(R.id.btnOrderInProgress);
        btnOrderInDelivery = findViewById(R.id.btnOrderInDelivery);
        btnOrderBuy = findViewById(R.id.btnOrderBuy);

        btnOrderBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setOrderStatus(Constants.ORDER_STATUS_RECEIVED);
                confirmOrder();
            }
        });
    }
}
