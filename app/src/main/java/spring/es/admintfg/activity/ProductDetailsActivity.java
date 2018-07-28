package spring.es.admintfg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.model.Product;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productDetailImage;
    private TextView productDetailName;
    private TextView productDetailDescription;
    private TextView productDetailPrice;
    private TextView productDetailStock;
    private Switch productVisible;

    public void getProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + getIntent().getStringExtra("productId");
        client.addHeader("Authorization", getIntent().getStringExtra("token"));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Product product = gson.fromJson(new String(responseBody), Product.class);
                GlideApp.with(getApplicationContext()).load(product.getProductImage().getUrl()).into(productDetailImage);
                productDetailName.setText(product.getName());
                productDetailDescription.setText(product.getDescription());
                productDetailPrice.setText(String.valueOf(product.getPrice()).concat(" ").concat(Constants.EURO));
                productDetailStock.setText(String.valueOf(product.getStockAvaiable()).concat(" uds"));
                productVisible.setChecked(product.isVisible());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productDetailImage = findViewById(R.id.productDetailImage);
        productDetailName = findViewById(R.id.productDetailName);
        productDetailDescription = findViewById(R.id.productDetailDescription);
        productDetailPrice = findViewById(R.id.productDetailPrice);
        productDetailStock = findViewById(R.id.productDetailStock);
        productVisible = findViewById(R.id.productVisible);

        getProductDetails();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(ProductDetailsActivity.this, EditProductActivity.class);
                changeActivity.putExtra("productId", getIntent().getStringExtra("productId"));
                changeActivity.putExtra("token", getIntent().getStringExtra("token"));
                startActivity(changeActivity);
            }
        });
    }
}
