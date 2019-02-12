package spring.es.admintfg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.model.Image;
import spring.es.admintfg.model.Product;

public class NewProductActivity extends AppCompatActivity {
    private EditText newProductName;
    private EditText newProductDescription;
    private EditText newProductPrice;
    private EditText newProductStock;

    private void updateProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getIntent().getStringExtra(Constants.TOKEN));
        Product newProduct = new Product();
        newProduct.setName(newProductName.getText().toString());
        newProduct.setDescription(newProductDescription.getText().toString());
        newProduct.setPrice(Double.valueOf(newProductPrice.getText().toString()));
        newProduct.setStockAvaiable(Integer.valueOf(newProductStock.getText().toString()));
        newProduct.setProductImage(new Image());

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        StringEntity stringProduct = new StringEntity(gson.toJson(newProduct, Product.class), StandardCharsets.UTF_8);

        client.post(getApplicationContext(), url, stringProduct, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent changeActivity = new Intent(NewProductActivity.this, MainActivity.class);
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                String productString = new String(responseBody, StandardCharsets.UTF_8);
                Product product = gson.fromJson(productString, Product.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, product.getId());
                changeActivity.putExtra(Constants.TOKEN, getIntent().getStringExtra(Constants.TOKEN));
                startActivity(changeActivity);
                Toast.makeText(getApplicationContext(), "Producto añadido correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        Toolbar newProductToolbar = findViewById(R.id.newProductToolbar);
        newProductToolbar.setTitle("Nuevo producto");
        newProductToolbar.setTitleMarginStart(100);
        newProductToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton newProductBackBtn = findViewById(R.id.newProductBackBtn);
        newProductBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(NewProductActivity.this, MainActivity.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, getIntent().getStringExtra(Constants.PRODUCT_ID));
                changeActivity.putExtra(Constants.TOKEN, getIntent().getStringExtra(Constants.TOKEN));
                startActivity(changeActivity);
            }
        });

        newProductName = findViewById(R.id.newProductName);
        newProductDescription = findViewById(R.id.newProductDescription);
        newProductPrice = findViewById(R.id.newProductPrice);
        newProductStock = findViewById(R.id.newProductStock);
        Button btnSaveNewProduct = findViewById(R.id.btnSaveNewProduct);

        btnSaveNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    updateProductDetails();
                }
            }
        });
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(newProductName.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "El nombre no puede estar vacío.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newProductDescription.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "La descricpión no puede estar vacía.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newProductPrice.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "El precio no puede estar vacío.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newProductStock.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "El stock no puede estar vacío.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}