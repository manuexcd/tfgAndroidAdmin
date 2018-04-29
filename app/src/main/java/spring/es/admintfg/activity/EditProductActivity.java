package spring.es.admintfg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.R;
import spring.es.admintfg.model.Image;
import spring.es.admintfg.model.Product;

public class EditProductActivity extends AppCompatActivity {
    private ImageView editProductDetailImage;
    private EditText editProductDetailName;
    private EditText editProductDetailDescription;
    private EditText editProductDetailPrice;
    private EditText editProductDetailStock;
    private Button btnSave;

    public void getEditProductDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + "products/" + getIntent().getStringExtra("productId");
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Product product = gson.fromJson(new String(responseBody), Product.class);
                GlideApp.with(getApplicationContext()).load(product.getProductImage().getUrl()).into(editProductDetailImage);
                editProductDetailName.setText(product.getName());
                editProductDetailDescription.setText(product.getDescription());
                editProductDetailPrice.setText(String.valueOf(product.getPrice()) + "€");
                editProductDetailStock.setText(String.valueOf(product.getStockAvaiable()) + " uds");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void updateProductDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        String productId = getIntent().getStringExtra("productId");
        String url = Constants.IP_ADDRESS + "products/" + productId;
        Product productUpdated = new Product();
        productUpdated.setId(Long.parseLong(productId));
        productUpdated.setName(editProductDetailName.getText().toString());
        productUpdated.setDescription(editProductDetailDescription.getText().toString());
        productUpdated.setPrice(Double.parseDouble(editProductDetailPrice.getText().toString().replace("€", "")));
        productUpdated.setStockAvaiable(Integer.parseInt(editProductDetailStock.getText().toString().replace(" uds", "")));
        productUpdated.setProductImage(new Image());

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        StringEntity stringProduct = new StringEntity(gson.toJson(productUpdated, Product.class), "UTF-8");

        client.put(getApplicationContext(), url, stringProduct, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent changeActivity = new Intent(EditProductActivity.this, ProductDetailsActivity.class);
                changeActivity.putExtra("productId", getIntent().getStringExtra("productId"));
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

        editProductDetailImage = findViewById(R.id.editProductDetailImage);
        editProductDetailName = findViewById(R.id.editProductDetailName);
        editProductDetailDescription = findViewById(R.id.editProductDetailDescription);
        editProductDetailPrice = findViewById(R.id.editProductDetailPrice);
        editProductDetailStock = findViewById(R.id.editProductDetailStock);

        getEditProductDetails();

        btnSave = findViewById(R.id.btnEditProduct);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductDetails();
            }
        });
    }
}
