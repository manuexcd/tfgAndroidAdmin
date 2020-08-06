package spring.es.admintfg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.PaginationScrollListener;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.LoginActivity;
import spring.es.admintfg.adapter.OrdersAdapter;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.pagination.OrdersPage;

public class OrdersFragment extends Fragment {
    private static ArrayList<OrderDTO> ordersArray;
    private OrdersAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyApplication app;
    private ProgressBar progressBar;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private String lastSearch;

    public void setOrdersList(byte[] responseBody) {
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        OrdersPage ordersPage = gson.fromJson(new String(responseBody), OrdersPage.class);
        ordersArray = new ArrayList<>(ordersPage.getContent());
        TOTAL_PAGES = ordersPage.getTotalPages();
        if (ordersPage.getFirst())
            mAdapter.setOrders(ordersArray);
        else
            mAdapter.getOrders().addAll(ordersArray);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        TOTAL_PAGES = ordersPage.getTotalPages();
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        isLastPage = ordersPage.getLast();
    }

    public void getOrdersByDateAsc(final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PATH_DATE_ASC + Constants.PARAM_PAGE + page + "&userId=";
        if (!app.isAdmin())
            url = url + app.getUserLogged().getId();

        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setOrdersList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getOrdersByDateDesc(final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PATH_DATE_DESC + Constants.PARAM_PAGE + page + "&userId=";
        if (!app.isAdmin())
            url = url + app.getUserLogged().getId();

        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setOrdersList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getOrdersByParam(String param, final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PATH_PARAM + param + Constants.PARAM_PAGE + page + "&userId=";
        if (!app.isAdmin())
            url = url + app.getUserLogged().getId();

        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setOrdersList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_orders, container, false);

        app = (MyApplication) Objects.requireNonNull(this.getActivity()).getApplication();

        RecyclerView ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        progressBar = view.findViewById(R.id.main_progress);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        ordersRecyclerView.setLayoutManager(layoutManager);
        ordersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new OrdersAdapter(ordersArray, view.getContext());
        ordersRecyclerView.setAdapter(mAdapter);

        EditText editTextSearchOrder = view.findViewById(R.id.editTextSearchOrder);

        editTextSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(Constants.EMPTY_STRING)) {
                    getOrdersByParam(s.toString(), PAGE_START);
                    lastSearch = Constants.LAST_SEARCH_PARAM;
                } else {
                    getOrdersByDateDesc(PAGE_START);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Spinner spinner = view.findViewById(R.id.spinnerOrders);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.spinner_orders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    getOrdersByDateDesc(PAGE_START);
                    lastSearch = Constants.PATH_DATE_DESC;
                    currentPage = PAGE_START;
                } else if (position == 1) {
                    getOrdersByDateAsc(PAGE_START);
                    lastSearch = Constants.PATH_DATE_ASC;
                    currentPage = PAGE_START;
                } else if (position == 2) {
                    getOrdersByDateAsc(PAGE_START);
                    lastSearch = Constants.PATH_DATE_ASC;
                    currentPage = PAGE_START;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                try {
                    getOrdersByDateDesc(PAGE_START);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ordersRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                if (Constants.PATH_DATE_ASC.equals(lastSearch)) {
                    getOrdersByDateAsc(currentPage);
                } else {
                    getOrdersByDateDesc(currentPage);
                }
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshOrders);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        isLastPage = false;
                        currentPage = 0;
                        getOrdersByDateDesc(PAGE_START);
                    }
                }
        );

        return view;
    }
}
