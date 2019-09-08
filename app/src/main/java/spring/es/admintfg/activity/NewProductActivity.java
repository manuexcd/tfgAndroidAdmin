package spring.es.admintfg.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

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
    private ImageView newProductImage;
    private File image;
    private ImageDTO imageCreated;
    private ImageButton btnDeleteImage;

    private void createProduct() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getIntent().getStringExtra(Constants.TOKEN));
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName(newProductName.getText().toString());
        newProduct.setDescription(newProductDescription.getText().toString());
        newProduct.setPrice(Double.valueOf(newProductPrice.getText().toString()));
        newProduct.setStockAvailable(Integer.valueOf(newProductStock.getText().toString()));
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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if (statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(NewProductActivity.this, LoginActivity.class));
            }
        });
    }

    public void createImage(String imageUrl) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_IMAGES;

        ImageDTO imageDto = new ImageDTO();
        imageDto.setUrl(imageUrl);

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        StringEntity stringImage = new StringEntity(gson.toJson(imageDto, ImageDTO.class), Charset.defaultCharset());

        client.post(getApplicationContext(), url, stringImage, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                imageCreated = gson.fromJson(response, ImageDTO.class);
                createProduct();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if (statusCode == 500 && response.contains("expired"))
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
        newProductImage = findViewById(R.id.newProductImage);
        Button btnSaveNewProduct = findViewById(R.id.btnSaveNewProduct);

        btnSaveNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    try {
                        if (image != null)
                            createImage(uploadFile(image, Constants.GOOGLE_CLOUD_BUCKET_NAME));
                        else
                            createProduct();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    @SuppressWarnings("deprecation")
    public String uploadFile(File file, final String bucketName) throws IOException {
        Credentials credentials = GoogleCredentials
                .fromStream(getApplicationContext().getResources().openRawResource(R.raw.serviceaccount));

        Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
                .setProjectId(Constants.GOOGLE_CLOUD_PROJECT_ID).build().getService();

        BlobInfo blobInfo =
                storage.create(
                        BlobInfo
                                .newBuilder(bucketName, file.getName())
                                .setAcl(new ArrayList<>(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                                .build(), new FileInputStream(file));
        return blobInfo.getMediaLink();
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