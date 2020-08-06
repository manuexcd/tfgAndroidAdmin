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
import spring.es.admintfg.dto.UserDTO;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView editProfileImage;
    private EditText editProfileName;
    private EditText editProfileSurname;
    private EditText editProfilePhone;
    private EditText editProfileEmail;
    private EditText editProfileAddress;
    private MyApplication app;
    private File image;
    private String imageUrl;
    private ImageButton btnDeleteImage;

    public void getUserDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + app.getUserLogged().getId();
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                UserDTO user = gson.fromJson(new String(responseBody), UserDTO.class);
                editProfileName.setText(user.getName());
                editProfileSurname.setText(user.getSurname());
                editProfilePhone.setText(user.getPhone());
                editProfileEmail.setText(user.getEmail());
                editProfileAddress.setText(user.getAddress());
                if (user.getUserImage() != null) {
                    GlideApp.with(getApplicationContext()).load(user.getUserImage()).into(editProfileImage);
                    btnDeleteImage.setVisibility(View.VISIBLE);
                } else
                    btnDeleteImage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void updateUserProfile() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + app.getUserLogged().getId();
        UserDTO userUpdated = new UserDTO();
        userUpdated.setName(editProfileName.getText().toString());
        userUpdated.setSurname(editProfileSurname.getText().toString());
        userUpdated.setPhone(editProfilePhone.getText().toString());
        userUpdated.setEmail(editProfileEmail.getText().toString());
        userUpdated.setAddress(editProfileAddress.getText().toString());
        userUpdated.setId(app.getUserLogged().getId());
        userUpdated.setRoles(app.getUserLogged().getRoles());
        userUpdated.setPassword(app.getUserLogged().getPassword());
        userUpdated.setUserImage(imageUrl);

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        StringEntity stringUser = new StringEntity(gson.toJson(userUpdated, UserDTO.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringUser, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringUserUpdated), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
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
                updateUserProfile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        app = (MyApplication) this.getApplication();

        editProfileImage = findViewById(R.id.profileImage);
        editProfileName = findViewById(R.id.editProfileName);
        editProfileSurname = findViewById(R.id.editProfileSurname);
        editProfilePhone = findViewById(R.id.editProfilePhone);
        editProfileEmail = findViewById(R.id.editProfileEmail);
        editProfileAddress = findViewById(R.id.editProfileAddress);

        Toolbar editProfileToolbar = findViewById(R.id.editProfileToolbar);
        editProfileToolbar.setTitle(getResources().getString(R.string.stringUpdateProfile));
        editProfileToolbar.setTitleMarginStart(100);
        editProfileToolbar.setTitleTextColor(Color.WHITE);

        Button btnImage = findViewById(R.id.btnImageEditProfile);
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

        btnDeleteImage = findViewById(R.id.btnDeleteImageEditProfile);
        btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileImage.setImageURI(null);
                image = null;
                imageUrl = null;
                btnDeleteImage.setVisibility(View.INVISIBLE);
            }
        });

        final ImageButton editProfileBackBtn = findViewById(R.id.editProfileBackBtn);
        editProfileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        FloatingActionButton btnSave = findViewById(R.id.fabEditProfile);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    if (image != null) {
                        try {
                            createImage(image);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else
                        updateUserProfile();
                }
            }
        });

        getUserDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            editProfileImage.setImageURI(selectedImage);
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
        if (editProfileName.getText().toString().equals("")) {
            editProfileName.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyName), Toast.LENGTH_LONG).show();
            return false;
        }
        if (editProfileSurname.getText().toString().equals("")) {
            editProfileSurname.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptySurname), Toast.LENGTH_LONG).show();
            return false;
        }
        if (editProfileEmail.getText().toString().equals("")) {
            editProfileEmail.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyEmail), Toast.LENGTH_LONG).show();
            return false;
        }
        if (editProfilePhone.getText().toString().equals("")) {
            editProfilePhone.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyPhone), Toast.LENGTH_LONG).show();
            return false;
        }
        if (editProfileAddress.getText().toString().equals("")) {
            editProfileAddress.setHighlightColor(getColor(R.color.red));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringEmptyAddress), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
