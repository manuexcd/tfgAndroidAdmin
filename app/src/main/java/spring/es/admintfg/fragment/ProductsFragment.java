package spring.es.admintfg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.NewProductActivity;
import spring.es.admintfg.adapter.ProductsAdapter;
import spring.es.admintfg.model.Product;

public class ProductsFragment extends Fragment {
    private ProductsAdapter mAdapter;
    private ArrayList<Product> productsArray;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText editTextSearchProduct;

    public void getProducts() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS;
        client.addHeader("Authorization", getActivity().getIntent().getStringExtra("token"));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Product[] users = gson.fromJson(new String(responseBody), Product[].class);
                productsArray = new ArrayList<>(Arrays.asList(users));
                mAdapter.setProducts(productsArray);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                editTextSearchProduct.setHint(String.valueOf(productsArray.size()).concat(" contactos."));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getProductsOrderBy(String param) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + param;
        client.addHeader("Authorization", getActivity().getIntent().getStringExtra("token"));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Product[] products = gson.fromJson(new String(responseBody), Product[].class);
                productsArray = new ArrayList<>(Arrays.asList(products));
                mAdapter.setProducts(productsArray);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                editTextSearchProduct.setHint(String.valueOf(productsArray.size()).concat(" productos."));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getProductsByParam(String param) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + "search/" + param;
        client.addHeader("Authorization", getActivity().getIntent().getStringExtra("token"));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Product[] products = gson.fromJson(new String(responseBody), Product[].class);
                productsArray = new ArrayList<>(Arrays.asList(products));
                mAdapter.setProducts(productsArray);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteProduct(Long id) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + "products/" + id;
        client.addHeader("Authorization", getActivity().getIntent().getStringExtra("token"));
        client.delete(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), "Producto eliminado correctamente", Toast.LENGTH_LONG).show();
                getProducts();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "No se puede eliminar el producto", Toast.LENGTH_LONG).show();
                getProducts();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_products, container, false);

        RecyclerView productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAdapter = new ProductsAdapter(productsArray, view.getContext());
        productsRecyclerView.setAdapter(mAdapter);

        editTextSearchProduct = view.findViewById(R.id.editTextSearchProduct);

        editTextSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    getProducts();
                    //getProductsByParam(s.toString());
                } else {
                    getProducts();
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
                    getProductsOrderBy("name");
                else if (position == 1)
                    getProductsOrderBy("pricedesc");
                else if (position == 2)
                    getProductsOrderBy("price");
                else if (position == 3)
                    getProductsOrderBy("stock");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                try {
                    getProducts();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ItemTouchHelper productTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(productsArray, from, to);
                mAdapter.notifyItemMoved(from, to);
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProducts);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getProducts();
                    }
                }
        );

        FloatingActionButton fabNewProduct = view.findViewById(R.id.fabNewProduct);
        fabNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(view.getContext(), NewProductActivity.class);
                changeActivity.putExtra("token", Objects.requireNonNull(getActivity()).getIntent().getStringExtra("token"));
                startActivity(changeActivity);
            }
        });

        return view;
    }
}
