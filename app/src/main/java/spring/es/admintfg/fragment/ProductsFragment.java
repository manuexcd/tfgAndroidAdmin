package spring.es.admintfg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collections;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.PaginationScrollListener;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.NewProductActivity;
import spring.es.admintfg.adapter.ProductsAdapter;
import spring.es.admintfg.dto.ProductDTO;
import spring.es.admintfg.pagination.ProductsPage;

public class ProductsFragment extends Fragment {
    private static final int PAGE_START = 0;
    private ProductsAdapter mAdapter;
    private ArrayList<ProductDTO> productsArray;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText editTextSearchProduct;
    private ProgressBar progressBar;
    private FloatingActionButton fabNewProduct;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private ItemTouchHelper productTouchHelper;

    public void setProductsList(byte[] responseBody) {
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        ProductsPage productsPage = gson.fromJson(new String(responseBody), ProductsPage.class);
        productsArray = new ArrayList<>(productsPage.getContent());
        TOTAL_PAGES = productsPage.getTotalPages();
        if (productsPage.getFirst())
            mAdapter.setProducts(productsArray);
        else if (!isLastPage)
            mAdapter.getProducts().addAll(productsArray);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        editTextSearchProduct.setHint(String.valueOf(productsArray.size()).concat(" productos."));
        TOTAL_PAGES = productsPage.getTotalPages();
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        isLastPage = productsPage.getLast();
    }

    public void getProducts(final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + Constants.PARAM_PAGE + page;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getActivity().getIntent().getStringExtra(Constants.TOKEN));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setProductsList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getProductsOrderBy(String param, final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + param + Constants.PARAM_PAGE + page;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getActivity().getIntent().getStringExtra(Constants.TOKEN));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setProductsList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getProductsByParam(String param, final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + Constants.PATH_SEARCH + param + Constants.PARAM_PAGE + page;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getActivity().getIntent().getStringExtra(Constants.TOKEN));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setProductsList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteProduct(Long id) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + id;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getActivity().getIntent().getStringExtra(Constants.TOKEN));
        client.delete(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), "Producto eliminado correctamente", Toast.LENGTH_LONG).show();
                getProducts(PAGE_START);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "No se puede eliminar el producto", Toast.LENGTH_LONG).show();
                //getProducts(PAGE_START);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_products, container, false);

        RecyclerView productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        progressBar = view.findViewById(R.id.main_progress);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        productsRecyclerView.setLayoutManager(layoutManager);
        productsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ProductsAdapter(productsArray, view.getContext());
        productsRecyclerView.setAdapter(mAdapter);

        editTextSearchProduct = view.findViewById(R.id.editTextSearchProduct);

        editTextSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(Constants.EMPTY_STRING)) {
                    getProductsByParam(s.toString(), PAGE_START);
                } else {
                    getProducts(PAGE_START);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Spinner spinner = view.findViewById(R.id.spinnerOrderProducts);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.order_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    getProductsOrderBy(Constants.ORDER_NAME, PAGE_START);
                else if (position == 1)
                    getProductsOrderBy(Constants.ORDER_PRICE_DESC, PAGE_START);
                else if (position == 2)
                    getProductsOrderBy(Constants.ORDER_PRICE_ASC, PAGE_START);
                else if (position == 3)
                    getProductsOrderBy(Constants.ORDER_STOCK, PAGE_START);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                try {
                    getProducts(PAGE_START);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        productTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(productsArray, from, to);
                mAdapter.notifyItemMoved(from, to);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Long productId = productsArray.get(viewHolder.getAdapterPosition()).getId();
                productsArray.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                try {
                    deleteProduct(productId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        productTouchHelper.attachToRecyclerView(productsRecyclerView);

        productsRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getProducts(currentPage);
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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProducts);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        isLastPage = false;
                        currentPage = 0;
                        getProducts(PAGE_START);
                    }
                }
        );

        fabNewProduct = view.findViewById(R.id.fabNewProduct);
        fabNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(view.getContext(), NewProductActivity.class);
                changeActivity.putExtra(Constants.TOKEN, Objects.requireNonNull(getActivity()).getIntent().getStringExtra(Constants.TOKEN));
                startActivity(changeActivity);
            }
        });

        return view;
    }

    public void onStart() {
        if (Objects.requireNonNull(getActivity()).getIntent().getStringExtra(Constants.HEADER_ADMIN) != null && getActivity().getIntent().getStringExtra(Constants.HEADER_ADMIN).equals(Constants.FALSE)) {
            fabNewProduct.hide();
            productTouchHelper.attachToRecyclerView(null);
        }
        super.onStart();
    }
}
