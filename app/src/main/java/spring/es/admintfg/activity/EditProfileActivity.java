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
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.UserDTO;

public class EditProfileActivity extends AppCompatActivity {
    //private ImageView editProfileImage;
    private EditText editProfileName;
    private EditText editProfileSurname;
    private EditText editProfilePhone;
    private EditText editProfileEmail;
    private EditText editProfileAddress;
    private MyApplication app;

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
        userUpdated.setUserImage(app.getUserLogged().getUserImage());

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
        StringEntity stringUser = new StringEntity(gson.toJson(userUpdated, UserDTO.class), StandardCharsets.UTF_8);

        client.put(getApplicationContext(), url, stringUser, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                Toast.makeText(getApplicationContext(), "User updated successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        app = (MyApplication) this.getApplication();

        editProfileName = findViewById(R.id.editProfileName);
        editProfileName.setText(app.getUserLogged().getName());
        editProfileSurname = findViewById(R.id.editProfileSurname);
        editProfileSurname.setText(app.getUserLogged().getSurname());
        editProfilePhone = findViewById(R.id.editProfilePhone);
        editProfilePhone.setText(app.getUserLogged().getPhone());
        editProfileEmail = findViewById(R.id.editProfileEmail);
        editProfileEmail.setText(app.getUserLogged().getEmail());
        editProfileAddress = findViewById(R.id.editProfileAddress);
        editProfileAddress.setText(app.getUserLogged().getAddress());

        Toolbar editProductToolbar = findViewById(R.id.editProfileToolbar);
        editProductToolbar.setTitle("Editar perfil");
        editProductToolbar.setTitleMarginStart(100);
        editProductToolbar.setTitleTextColor(Color.WHITE);

        final ImageButton editProfileBackBtn = findViewById(R.id.editProfileBackBtn);
        editProfileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            }
        });


        FloatingActionButton btnSave = findViewById(R.id.fabEditProfile);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }
}
