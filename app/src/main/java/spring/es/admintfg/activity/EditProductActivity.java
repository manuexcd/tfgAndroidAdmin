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
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.ImageDTO;
import spring.es.admintfg.dto.ProductDTO;

public class EditProductActivity extends AppCompatActivity {
    private ImageView editProductDetailImage;
    private EditText editProductDetailName;
    private EditText editProductDetailDescription;
    private EditText editProductDetailPrice;
    private EditText editProductDetailStock;
    private ImageDTO productImage;
    private MyApplication app;

    public void getEditProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + getIntent().getStringExtra(Constants.PRODUCT_ID);
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                ProductDTO product = gson.fromJson(new String(responseBody), ProductDTO.class);
                if (product.getProductImage() != null)
                    GlideApp.with(getApplicationContext()).load(product.getProductImage().getUrl()).into(editProductDetailImage);
                editProductDetailName.setText(product.getName());
                editProductDetailDescription.setText(product.getDescription());
                editProductDetailPrice.setText(String.valueOf(product.getPrice()).concat(Constants.EURO));
                editProductDetailStock.setText(String.valueOf(product.getStockAvailable()).concat(" uds"));
                productImage = product.getProductImage();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(EditProductActivity.this, LoginActivity.class));
            }
        });
    }

    public void updateProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        String productId = getIntent().getStringExtra(Constants.PRODUCT_ID);
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + productId;
        ProductDTO productUpdated = new ProductDTO();
        productUpdated.setProductImage(productImage);
        productUpdated.setId(Long.parseLong(productId));
        productUpdated.setName(editProductDetailName.getText().toString());
        productUpdated.setDescription(editProductDetailDescription.getText().toString());
        productUpdated.setPrice(Double.parseDouble(editProductDetailPrice.getText().toString().replace(Constants.EURO, "")));
        productUpdated.setStockAvailable(Integer.parseInt(editProductDetailStock.getText().toString().replace(" uds", "")));

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        StringEntity stringProduct = new StringEntity(gson.toJson(productUpdated, ProductDTO.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringProduct, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent changeActivity = new Intent(EditProductActivity.this, ProductDetailsActivity.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, getIntent().getStringExtra(Constants.PRODUCT_ID));
                startActivity(changeActivity);
                Toast.makeText(getApplicationContext(), "Product updated successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(EditProductActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        app = (MyApplication) this.getApplication();

        Toolbar editProductToolbar = findViewById(R.id.editProductToolbar);
        editProductToolbar.setTitle("Editar producto");
        editProductToolbar.setTitleMarginStart(100);
        editProductToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton editProductBackBtn = findViewById(R.id.editProductBackBtn);
        editProductBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(EditProductActivity.this, ProductDetailsActivity.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, getIntent().getStringExtra(Constants.PRODUCT_ID));
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
