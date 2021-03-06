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
import spring.es.admintfg.dto.ImageDTO;
import spring.es.admintfg.dto.ProductDTO;

public class NewProductActivity extends AppCompatActivity {
    private EditText newProductName;
    private EditText newProductDescription;
    private EditText newProductPrice;
    private EditText newProductStock;

    private void updateProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getIntent().getStringExtra(Constants.TOKEN));
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName(newProductName.getText().toString());
        newProduct.setDescription(newProductDescription.getText().toString());
        newProduct.setPrice(Double.valueOf(newProductPrice.getText().toString()));
        newProduct.setStockAvailable(Integer.valueOf(newProductStock.getText().toString()));
        newProduct.setProductImage(new ImageDTO());

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        StringEntity stringProduct = new StringEntity(gson.toJson(newProduct, ProductDTO.class), StandardCharsets.UTF_8);

        client.post(getApplicationContext(), url, stringProduct, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent changeActivity = new Intent(NewProductActivity.this, MainActivity.class);
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                String productString = new String(responseBody, StandardCharsets.UTF_8);
                ProductDTO product = gson.fromJson(productString, ProductDTO.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, product.getId());
                changeActivity.putExtra(Constants.TOKEN, getIntent().getStringExtra(Constants.TOKEN));
                startActivity(changeActivity);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringProductAdded), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(NewProductActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        Toolbar newProductToolbar = findViewById(R.id.newProductToolbar);
        newProductToolbar.setTitle(getResources().getString(R.string.stringNewProduct));
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyName), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newProductDescription.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyDescription), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newProductPrice.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyPrice), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newProductStock.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyStock), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}