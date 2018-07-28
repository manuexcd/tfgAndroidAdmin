package spring.es.admintfg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import spring.es.admintfg.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUserName;
    private EditText registerUserSurname;
    private EditText registerUserEmail;
    private EditText registerUserPhone;
    private EditText registerUserAddress;
    private EditText registerUserPassword;

    public void registerUser() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + "registration";

        User user = new User();
        user.setName(registerUserName.getText().toString());
        user.setSurname(registerUserSurname.getText().toString());
        user.setEmail(registerUserEmail.getText().toString());
        user.setPhone(registerUserPhone.getText().toString());
        user.setAddress(registerUserAddress.getText().toString());
        user.setPassword(registerUserPassword.getText().toString());

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        StringEntity stringUser = new StringEntity(gson.toJson(user, User.class), "UTF-8");

        client.post(getApplicationContext(), url, stringUser, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), "Registro completado.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
        final EditText registerUserPasswordRepeat = findViewById(R.id.registerUserPasswordRepeat);
        Button btnSaveUser = findViewById(R.id.btnSaveUser);

        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(registerUserPassword.getText().toString().equals(registerUserPasswordRepeat.getText().toString()))
                    registerUser();
                else {
                    registerUserPassword.setHighlightColor(getColor(R.color.red));
                    Toast.makeText(getApplicationContext(), "Las constraseñas no coinciden.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
