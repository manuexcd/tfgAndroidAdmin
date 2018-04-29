package spring.es.admintfg.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.R;
import spring.es.admintfg.adapter.ProductsAdapter;
import spring.es.admintfg.model.Product;

public class ProductsActivity extends AppCompatActivity {
    private RecyclerView productsRecyclerView;
    private ProductsAdapter mAdapter;
    private static ArrayList<Product> productsArray;
    private static ArrayAdapter<Product> productsAdapter;

    public void getProducts() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + "products";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Product[] products = gson.fromJson(new String(responseBody), Product[].class);
                productsArray = new ArrayList<Product>(Arrays.asList(products));
                productsAdapter = new ArrayAdapter<Product>(ProductsActivity.this, android.R.layout.simple_list_item_activated_1, productsArray);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getProducts();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ProductsAdapter(productsArray, this);
        productsRecyclerView.setAdapter(mAdapter);

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

            }
        });

        productTouchHelper.attachToRecyclerView(productsRecyclerView);
    }
}
