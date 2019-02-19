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
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.UserDTO;

/**
 * Created by manue on 18/02/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText user;
    private EditText password;
    private boolean isAdmin = false;
    private Intent changeActivity;

    private void login() throws Exception {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_LOGIN;

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("email", user.getText().toString());
        jsonParams.put("password", password.getText().toString());

        StringEntity stringBody = new StringEntity(jsonParams.toString(), Charset.defaultCharset());
        stringBody.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()));

        client.post(getApplicationContext(), url, stringBody, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Header> listHeaders = new ArrayList<>(Arrays.asList(headers));
                changeActivity = new Intent(LoginActivity.this, MainActivity.class);
                String token = "";
                for (Header header : listHeaders) {
                    if (header.getName().equals(Constants.HEADER_AUTHORIZATION)) {
                        token = header.getValue();
                        changeActivity.putExtra(Constants.TOKEN, token);
                    }
                }

                String urlDetails = Constants.IP_ADDRESS + Constants.PATH_USERS + Constants.PATH_EMAIL + user.getText().toString();
                AsyncHttpClient clientDetails = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
                clientDetails.addHeader(Constants.HEADER_AUTHORIZATION, token);
                clientDetails.get(urlDetails, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Gson gsonDetails = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                        UserDTO userDetails = gsonDetails.fromJson(new String(responseBody), UserDTO.class);
                        isAdmin = userDetails.getRoles().contains(Constants.ADMIN_ROLE);
                        changeActivity.putExtra(Constants.HEADER_ADMIN, String.valueOf(isAdmin));
                        startActivity(changeActivity);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }
                });
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
        setContentView(R.layout.activity_login);

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