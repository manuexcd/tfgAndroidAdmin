package spring.es.admintfg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.R;
import spring.es.admintfg.RecyclerItemClickListener;
import spring.es.admintfg.activity.OrderDetailsActivity;
import spring.es.admintfg.adapter.OrdersAdapter;
import spring.es.admintfg.model.Order;

public class OrdersFragment extends Fragment {
    private static ArrayList<Order> ordersArray;
    private OrdersAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public void getOrders() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Order[] orders = gson.fromJson(new String(responseBody), Order[].class);
                ordersArray = new ArrayList<>(Arrays.asList(orders));
                mAdapter.setOrders(ordersArray);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*public void getOrdersByParam(String param) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + "param/" + Long.valueOf(param);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Order[] orders = gson.fromJson(new String(responseBody), Order[].class);
                ordersArray = new ArrayList<>(Arrays.asList(orders));
                mAdapter.setOrders(ordersArray);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_orders, container, false);

        EditText editTextSearchOrder = view.findViewById(R.id.editTextSearchOrder);

        editTextSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAdapter = new OrdersAdapter(ordersArray, view.getContext());
        ordersRecyclerView.setAdapter(mAdapter);

        getOrders();

        ordersRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), ordersRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Order currentOrder = ordersArray.get(position);

                        Intent detailIntent = new Intent(view.getContext(), OrderDetailsActivity.class);
                        detailIntent.putExtra("orderId", String.valueOf(currentOrder.getId()));
                        view.getContext().startActivity(detailIntent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshOrders);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getOrders();
                    }
                }
        );

        return view;
    }
}
