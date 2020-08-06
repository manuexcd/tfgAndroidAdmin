package spring.es.admintfg.activity;

import android.content.Intent;
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

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.UserDTO;

/**
 * Created by manue on 18/02/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText user;
    private EditText password;
    private Intent changeActivity;
    private MyApplication app;

    private void login() throws Exception {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_LOGIN;

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(Constants.EMAIL, user.getText().toString());
        jsonParams.put(Constants.PASSWORD, password.getText().toString());

        StringEntity stringBody = new StringEntity(jsonParams.toString(), Charset.defaultCharset());
        stringBody.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()));

        client.post(getApplicationContext(), url, stringBody, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Header> listHeaders = new ArrayList<>(Arrays.asList(headers));
                changeActivity = new Intent(LoginActivity.this, MainActivity.class);
                for (Header header : listHeaders) {
                    if (header.getName().equals(Constants.HEADER_AUTHORIZATION)) {
                        app.setToken(header.getValue());
                        app.setPassword(password.getText().toString());
                    }
                }

                String urlDetails = Constants.IP_ADDRESS + Constants.PATH_USERS + Constants.PATH_EMAIL + user.getText().toString();
                AsyncHttpClient clientDetails = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
                clientDetails.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
                clientDetails.get(urlDetails, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Gson gsonDetails = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                        UserDTO userDetails = gsonDetails.fromJson(new String(responseBody), UserDTO.class);
                        app.setUserLogged(userDetails);
                        app.setAdmin(userDetails.getRoles().contains(Constants.ADMIN_ROLE));
                        startActivity(changeActivity);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401)
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringWrongUserOrPassword), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (MyApplication) this.getApplication();

        user = findViewById(R.id.inputUser);
        password = findViewById(R.id.inputPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent changeActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(changeActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}