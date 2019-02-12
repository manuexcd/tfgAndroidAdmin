package spring.es.admintfg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.model.Image;
import spring.es.admintfg.model.Product;

public class EditProductActivity extends AppCompatActivity {
    private Toolbar editProductToolbar;
    private ImageView editProductDetailImage;
    private EditText editProductDetailName;
    private EditText editProductDetailDescription;
    private EditText editProductDetailPrice;
    private EditText editProductDetailStock;
    private Image productImage;

    public void getEditProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + getIntent().getStringExtra(Constants.PRODUCT_ID);
        client.addHeader(Constants.HEADER_AUTHORIZATION, getIntent().getStringExtra(Constants.TOKEN));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                Product product = gson.fromJson(new String(responseBody), Product.class);
                if (product.getProductImage() != null)
                    GlideApp.with(getApplicationContext()).load(product.getProductImage().getUrl()).into(editProductDetailImage);
                editProductDetailName.setText(product.getName());
                editProductDetailDescription.setText(product.getDescription());
                editProductDetailPrice.setText(String.valueOf(product.getPrice()).concat(Constants.EURO));
                editProductDetailStock.setText(String.valueOf(product.getStockAvaiable()).concat(" uds"));
                productImage = product.getProductImage();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void updateProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        client.addHeader(Constants.HEADER_AUTHORIZATION, getIntent().getStringExtra(Constants.TOKEN));
        String productId = getIntent().getStringExtra(Constants.PRODUCT_ID);
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + productId;
        Product productUpdated = new Product();
        productUpdated.setProductImage(productImage);
        productUpdated.setId(Long.parseLong(productId));
        productUpdated.setName(editProductDetailName.getText().toString());
        productUpdated.setDescription(editProductDetailDescription.getText().toString());
        productUpdated.setPrice(Double.parseDouble(editProductDetailPrice.getText().toString().replace(Constants.EURO, "")));
        productUpdated.setStockAvaiable(Integer.parseInt(editProductDetailStock.getText().toString().replace(" uds", "")));

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        StringEntity stringProduct = new StringEntity(gson.toJson(productUpdated, Product.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringProduct, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent changeActivity = new Intent(EditProductActivity.this, ProductDetailsActivity.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, getIntent().getStringExtra(Constants.PRODUCT_ID));
                changeActivity.putExtra(Constants.TOKEN, getIntent().getStringExtra(Constants.TOKEN));
                startActivity(changeActivity);
                Toast.makeText(getApplicationContext(), "Product updated successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        editProductToolbar = findViewById(R.id.editProductToolbar);
        editProductToolbar.setTitle("Editar producto");
        editProductToolbar.setTitleMarginStart(100);
        editProductToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton editProductBackBtn = findViewById(R.id.editProductBackBtn);
        editProductBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(EditProductActivity.this, ProductDetailsActivity.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, getIntent().getStringExtra(Constants.PRODUCT_ID));
                changeActivity.putExtra(Constants.TOKEN, getIntent().getStringExtra(Constants.TOKEN));
                startActivity(changeActivity);
            }
        });

        editProductDetailImage = findViewById(R.id.editProductDetailImage);
        editProductDetailName = findViewById(R.id.editProductDetailName);
        editProductDetailDescription = findViewById(R.id.editProductDetailDescription);
        editProductDetailPrice = findViewById(R.id.editProductDetailPrice);
        editProductDetailStock = findViewById(R.id.editProductDetailStock);

        getEditProductDetails();

        Button btnSave = findViewById(R.id.btnEditProduct);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductDetails();
            }
        });
    }
}
