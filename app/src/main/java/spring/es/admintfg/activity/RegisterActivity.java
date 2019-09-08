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

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.ImageDTO;
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
    private ImageDTO imageCreated;
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
        if (imageCreated != null)
            user.setUserImage(imageCreated);
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
                Toast.makeText(getApplicationContext(), "Registro completado.", Toast.LENGTH_LONG).show();
                Intent changeActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(changeActivity);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if (statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
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
                registerUser();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if (statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
                        try {
                            if (image != null)
                                createImage(uploadFile(image, Constants.GOOGLE_CLOUD_BUCKET_NAME));
                            else
                                registerUser();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    registerUserPassword.setHighlightColor(getColor(R.color.red));
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
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
                                .setAcl(new ArrayList<>(Collections.singletonList(Acl.of(User.ofAllUsers(), Role.READER))))
                                .build(), new FileInputStream(file));
        return blobInfo.getMediaLink();
    }

    public Boolean validateInputs() {
        if (registerUserName.getText().toString().equals("")) {
            registerUserName.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), "Introduzca un valor para el nombre.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserSurname.getText().toString().equals("")) {
            registerUserSurname.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), "Introduzca un valor para el apellido.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserEmail.getText().toString().equals("")) {
            registerUserEmail.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), "Introduzca un valor para el email.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserPhone.getText().toString().equals("")) {
            registerUserPhone.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), "Introduzca un valor para el teléfono.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserAddress.getText().toString().equals("")) {
            registerUserAddress.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), "Introduzca un valor para la dirección.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (registerUserPassword.getText().toString().equals("")) {
            registerUserPassword.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), "Introduzca un valor para la contraseña.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
