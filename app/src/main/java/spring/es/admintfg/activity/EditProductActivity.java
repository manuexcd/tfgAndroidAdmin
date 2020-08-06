package spring.es.admintfg.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import de.hdodenhof.circleimageview.CircleImageView;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.ProductDTO;

public class EditProductActivity extends AppCompatActivity {
    private CircleImageView editProductDetailImage;
    private EditText editProductDetailName;
    private EditText editProductDetailDescription;
    private EditText editProductDetailPrice;
    private EditText editProductDetailStock;
    private String productImage;
    private MyApplication app;
    private File image;
    private ImageButton btnDeleteImage;

    public void getEditProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + getIntent().getStringExtra(Constants.PRODUCT_ID);
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                ProductDTO product = gson.fromJson(new String(responseBody), ProductDTO.class);
                editProductDetailName.setText(product.getName());
                editProductDetailDescription.setText(product.getDescription());
                editProductDetailPrice.setText(String.valueOf(product.getPrice()).concat(Constants.EURO));
                editProductDetailStock.setText(String.valueOf(product.getStockAvailable()).concat(" uds"));
                productImage = product.getProductImage();
                if (product.getProductImage() != null) {
                    btnDeleteImage.setVisibility(View.VISIBLE);
                    GlideApp.with(getApplicationContext()).load(product.getProductImage()).into(editProductDetailImage);
                } else {
                    btnDeleteImage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(EditProductActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void updateProductDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        String productId = getIntent().getStringExtra(Constants.PRODUCT_ID);
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS;
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringProductUpdated), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(EditProductActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void createImage(File image) throws FileNotFoundException {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + Constants.PATH_IMAGES;

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.addPart(image.getName(), new FileBody(image));

        RequestParams params = new RequestParams();
        params.put("file", image);

        client.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                productImage = new String(responseBody);
                updateProductDetails();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    String response = new String(responseBody);
                    if (statusCode == 500 && response.contains(Constants.EXPIRED)) {
                        startActivity(new Intent(EditProductActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        app = (MyApplication) this.getApplication();

        Toolbar editProductToolbar = findViewById(R.id.editProductToolbar);
        editProductToolbar.setTitle(getResources().getString(R.string.stringUpdateProduct));
        editProductToolbar.setTitleMarginStart(100);
        editProductToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton editProductBackBtn = findViewById(R.id.editProductBackBtn);
        editProductBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editProductDetailImage = findViewById(R.id.editProductDetailImage);
        editProductDetailName = findViewById(R.id.editProductDetailName);
        editProductDetailDescription = findViewById(R.id.editProductDetailDescription);
        editProductDetailPrice = findViewById(R.id.editProductDetailPrice);
        editProductDetailStock = findViewById(R.id.editProductDetailStock);

        getEditProductDetails();

        FloatingActionButton fabEditProduct = findViewById(R.id.fabEditProduct);
        fabEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    if (image != null) {
                        try {
                            createImage(image);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else
                        updateProductDetails();
                }
            }
        });

        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProductDetailImage.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(Constants.IMAGE_TYPE);
                String[] mimeTypes = {Constants.IMAGE_JPG, Constants.IMAGE_PNG};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, 0);
            }
        });

        btnDeleteImage = findViewById(R.id.btnDeleteImage);
        btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productImage = null;
                GlideApp.with(getApplicationContext()).load("").into(editProductDetailImage);
                btnDeleteImage.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            editProductDetailImage.setImageURI(selectedImage);
            btnDeleteImage.setVisibility(View.VISIBLE);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            assert selectedImage != null;
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            image = new File(picturePath);
        }
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(editProductDetailName.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyName), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(editProductDetailDescription.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyDescription), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(editProductDetailPrice.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyPrice), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(editProductDetailStock.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringNoEmptyStock), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
