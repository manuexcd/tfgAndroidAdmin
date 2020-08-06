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

public class NewProductActivity extends AppCompatActivity {
    private EditText newProductName;
    private EditText newProductDescription;
    private EditText newProductPrice;
    private EditText newProductStock;
    private CircleImageView newProductImage;
    private File image;
    private String imageCreated;
    private ImageButton btnDeleteImage;
    private MyApplication app;

    private void createProduct() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName(newProductName.getText().toString());
        newProduct.setDescription(newProductDescription.getText().toString());
        newProduct.setPrice(Double.parseDouble(newProductPrice.getText().toString()));
        newProduct.setStockAvailable(Integer.parseInt(newProductStock.getText().toString()));
        newProduct.setProductImage(imageCreated);

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
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(NewProductActivity.this, LoginActivity.class));
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
                imageCreated = new String(responseBody);
                createProduct();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(NewProductActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        app = (MyApplication) this.getApplication();

        Toolbar newProductToolbar = findViewById(R.id.newProductToolbar);
        newProductToolbar.setTitle(getResources().getString(R.string.stringNewProduct));
        newProductToolbar.setTitleMarginStart(100);
        newProductToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton newProductBackBtn = findViewById(R.id.newProductBackBtn);
        newProductBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        newProductName = findViewById(R.id.newProductName);
        newProductDescription = findViewById(R.id.newProductDescription);
        newProductPrice = findViewById(R.id.newProductPrice);
        newProductStock = findViewById(R.id.newProductStock);
        newProductImage = findViewById(R.id.newProductImage);
        FloatingActionButton btnSaveNewProduct = findViewById(R.id.btnSaveNewProduct);

        btnSaveNewProduct.setOnClickListener(new View.OnClickListener() {
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
                        createProduct();
                }
            }
        });

        Button btnImage = findViewById(R.id.btnImage);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(Constants.IMAGE_TYPE);
                String[] mimeTypes = {Constants.IMAGE_JPG, Constants.IMAGE_PNG};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, 0);
            }
        });

        btnDeleteImage = findViewById(R.id.btnDeleteImage);
        btnDeleteImage.setVisibility(View.INVISIBLE);
        btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newProductImage.setImageURI(null);
                GlideApp.with(getApplicationContext()).load("").into(newProductImage);
                btnDeleteImage.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            newProductImage.setImageURI(selectedImage);
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