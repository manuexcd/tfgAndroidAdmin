package spring.es.admintfg.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.UserDTO;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerUserName;
    private EditText registerUserSurname;
    private EditText registerUserEmail;
    private EditText registerUserPhone;
    private EditText registerUserAddress;
    private EditText registerUserPassword;
    private Switch switchUserRole;
    private ImageView registerUserImage;
    private File image;
    private String imageUrl;
    private ImageButton btnDeleteImage;

    public void registerUser() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + Constants.PATH_SIGN_IN;

        UserDTO user = new UserDTO();
        user.setName(registerUserName.getText().toString());
        user.setSurname(registerUserSurname.getText().toString());
        user.setEmail(registerUserEmail.getText().toString());
        user.setPhone(registerUserPhone.getText().toString());
        user.setAddress(registerUserAddress.getText().toString());
        user.setPassword(registerUserPassword.getText().toString());
        if (imageUrl != null)
            user.setUserImage(imageUrl);
        List<String> userRoles = new ArrayList<>();
        userRoles.add(Constants.BASIC_ROLE);
        if (switchUserRole.isChecked())
            userRoles.add(Constants.ADMIN_ROLE);
        user.setRoles(userRoles);

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        StringEntity stringUser = new StringEntity(gson.toJson(user, UserDTO.class), Charset.defaultCharset());

        client.post(getApplicationContext(), url, stringUser, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringSignInCompleted), Toast.LENGTH_LONG).show();
                Intent changeActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(changeActivity);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
                imageUrl = new String(responseBody);
                registerUser();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUserName = findViewById(R.id.registerUserName);
        registerUserSurname = findViewById(R.id.registerUserSurname);
        registerUserEmail = findViewById(R.id.registerUserEmail);
        registerUserPhone = findViewById(R.id.registerUserPhone);
        registerUserAddress = findViewById(R.id.registerUserAddress);
        registerUserPassword = findViewById(R.id.registerUserPassword);
        switchUserRole = findViewById(R.id.switchUserRole);
        registerUserImage = findViewById(R.id.registerUserImage);

        final EditText registerUserPasswordRepeat = findViewById(R.id.registerUserPasswordRepeat);
        Button btnSaveUser = findViewById(R.id.btnSaveUser);

        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerUserPassword.getText().toString().equals(registerUserPasswordRepeat.getText().toString())) {
                    if (validateInputs()) {
                        if (image != null) {
                            try {
                                createImage(image);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else
                            registerUser();
                    }
                } else {
                    registerUserPassword.setHighlightColor(getColor(R.color.red));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringPasswordNotMatch), Toast.LENGTH_LONG).show();
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
                registerUserImage.setImageURI(null);
                btnDeleteImage.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            registerUserImage.setImageURI(selectedImage);
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

    public Boolean validateInputs() {
        if (registerUserName.getText().toString().equals("")) {
            registerUserName.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyName), Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserSurname.getText().toString().equals("")) {
            registerUserSurname.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptySurname), Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserEmail.getText().toString().equals("")) {
            registerUserEmail.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyEmail), Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserPhone.getText().toString().equals("")) {
            registerUserPhone.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyPhone), Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserAddress.getText().toString().equals("")) {
            registerUserAddress.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyAddress), Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserPassword.getText().toString().equals("")) {
            registerUserPassword.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyPassword), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
