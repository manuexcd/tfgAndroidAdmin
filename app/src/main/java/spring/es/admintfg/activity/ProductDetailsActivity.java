package spring.es.admintfg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.OrderLineDTO;
import spring.es.admintfg.dto.ProductDTO;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productDetailImage;
    private TextView productDetailName;
    private TextView productDetailDescription;
    private TextView productDetailPrice;
    private TextView productDetailStock;
    private FloatingActionButton fab;
    //private Switch productVisible;
    private EditText productDetailInputStock;
    private ImageButton productDetailAddBtn;
    private OrderDTO temporalOrder;
    private ProductDTO product;
    private MyApplication app;

    public void getProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + getIntent().getStringExtra(Constants.PRODUCT_ID);
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                product = gson.fromJson(new String(responseBody), ProductDTO.class);
                if (product.getProductImage() != null)
                    GlideApp.with(getApplicationContext()).load(product.getProductImage()).into(productDetailImage);
                productDetailName.setText(product.getName());
                productDetailDescription.setText(product.getDescription());
                productDetailPrice.setText(getResources().getString(R.string.stringRecommendedPrice).concat(" ").concat(String.valueOf(product.getPrice()).concat(" ").concat(Constants.EURO)));
                productDetailStock.setText(String.valueOf(product.getStockAvailable()).concat(" uds"));
                //productVisible.setChecked(product.isVisible());
                if (!app.isAdmin()) {
                    if (product.getStockAvailable() > 0) {
                        productDetailStock.setText(getResources().getString(R.string.stringStock));
                        productDetailStock.setTextColor(Color.GREEN);
                        productDetailInputStock.setText(String.valueOf(1));

                    } else {
                        productDetailStock.setText(getResources().getString(R.string.stringNoStock));
                        productDetailStock.setTextColor(Color.RED);
                        productDetailInputStock.setVisibility(View.INVISIBLE);
                        productDetailAddBtn.setVisibility(View.INVISIBLE);
                    }
                } else {
                    productDetailInputStock.setVisibility(View.INVISIBLE);
                    productDetailAddBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains(Constants.EXPIRED))
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getTemporalOrder() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PATH_TEMPORAL + Constants.PARAM_USER_ID + app.getUserLogged().getId();
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                if (statusCode != HttpStatus.SC_NO_CONTENT)
                    temporalOrder = gson.fromJson(new String(responseBody), OrderDTO.class);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains(Constants.EXPIRED))
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createTemporalOrder(OrderDTO order) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PARAM_USER_ID + app.getUserLogged().getId();
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        StringEntity stringOrder = new StringEntity(gson.toJson(order, OrderDTO.class), StandardCharsets.UTF_8);

        client.post(getApplicationContext(), url, stringOrder, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringProductAdded), Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProductDetailsActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains(Constants.EXPIRED))
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTemporalOrder(OrderDTO order) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        StringEntity stringProduct = new StringEntity(gson.toJson(order, OrderDTO.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringProduct, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringProductAdded), Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProductDetailsActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains(Constants.EXPIRED))
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        app = (MyApplication) this.getApplication();

        Toolbar productDetailToolbar = findViewById(R.id.productDetailToolbar);
        productDetailToolbar.setTitle(getResources().getString(R.string.stringProductDetails));
        productDetailToolbar.setTitleMarginStart(150);
        productDetailToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton productDetailBackBtn = findViewById(R.id.productDetailBackBtn);
        productDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(ProductDetailsActivity.this, MainActivity.class);
                startActivity(changeActivity);
            }
        });

        productDetailImage = findViewById(R.id.productDetailImage);
        productDetailName = findViewById(R.id.productDetailName);
        productDetailDescription = findViewById(R.id.productDetailDescription);
        productDetailPrice = findViewById(R.id.productDetailPrice);
        productDetailStock = findViewById(R.id.productDetailStock);
        //productVisible = findViewById(R.id.productVisible);
        productDetailInputStock = findViewById(R.id.productDetailInputStock);
        productDetailAddBtn = findViewById(R.id.productDetailAddBtn);

        getProductDetails();
        getTemporalOrder();

        productDetailAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderLineDTO orderLine = new OrderLineDTO();
                orderLine.setQuantity(Integer.parseInt(productDetailInputStock.getText().toString()));
                orderLine.setProductId(product.getId());
                if (temporalOrder == null) {
                    OrderDTO order = new OrderDTO();
                    order.setOrderLines(Collections.singletonList(orderLine));
                    createTemporalOrder(order);
                } else {
                    temporalOrder.getOrderLines().add(orderLine);
                    updateTemporalOrder(temporalOrder);
                }
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changeActivity = new Intent(ProductDetailsActivity.this, EditProductActivity.class);
                changeActivity.putExtra(Constants.PRODUCT_ID, getIntent().getStringExtra(Constants.PRODUCT_ID));
                startActivity(changeActivity);
            }
        });
    }

    protected void onStart() {
        if (!app.isAdmin())
            fab.hide();
        super.onStart();
    }
}
