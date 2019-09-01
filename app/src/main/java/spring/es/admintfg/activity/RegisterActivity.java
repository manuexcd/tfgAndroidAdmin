package spring.es.admintfg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUserName = findViewById(R.id.registerUserName);
        registerUserSurname = findViewById(R.id.registerUserSurname);
        registerUserEmail = findViewById(R.id.registerUserEmail);
        registerUserPhone = findViewById(R.id.registerUserPhone);
        registerUserAddress = findViewById(R.id.registerUserAddress);
        registerUserPassword = findViewById(R.id.registerUserPassword);
        switchUserRole = findViewById(R.id.switchUserRole);
        final EditText registerUserPasswordRepeat = findViewById(R.id.registerUserPasswordRepeat);
        Button btnSaveUser = findViewById(R.id.btnSaveUser);

        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerUserPassword.getText().toString().equals(registerUserPasswordRepeat.getText().toString())) {
                    if (validateInputs())
                        registerUser();
                } else {
                    registerUserPassword.setHighlightColor(getColor(R.color.red));
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
                }
            }
        });
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
