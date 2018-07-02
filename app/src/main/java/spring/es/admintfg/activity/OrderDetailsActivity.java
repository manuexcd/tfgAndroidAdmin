package spring.es.admintfg.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
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

    public void getOrderDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + getIntent().getStringExtra("orderId");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Order order = gson.fromJson(new String(responseBody), Order.class);
                orderDetailId.setText(String.valueOf(order.getId()));
                orderDetailDate.setText(String.valueOf(order.getDate()));
                orderDetailUser.setText(order.getUser().getName().concat(" ").concat(order.getUser().getSurname()));
                orderDetailPrice.setText(String.valueOf(new DecimalFormat("#.##").format(order.getTotalPrice())).concat(" ").concat(Constants.EURO));
                orderLinesArray = new ArrayList<>(order.getOrderLines());
                mAdapter.setOrderLines(orderLinesArray);
                mAdapter.notifyDataSetChanged();
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

        Button btnOrderInProgress = findViewById(R.id.btnOrderInProgress);
        Button btnOrderInDelivery = findViewById(R.id.btnOrderInDelivery);
    }
}
