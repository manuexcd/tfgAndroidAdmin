package spring.es.admintfg;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

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
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.UserDTO;

public class MyApplication extends Application {

    private String token;
    private Boolean isAdmin = false;
    private UserDTO userLogged;
    private OrderDTO temporalOrder;
    private String password;

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public UserDTO getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(UserDTO userLogged) {
        this.userLogged = userLogged;
    }

    public OrderDTO getTemporalOrder() {
        return temporalOrder;
    }

    public void setTemporalOrder(OrderDTO temporalOrder) {
        this.temporalOrder = temporalOrder;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login(String username, String password) throws Exception {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_LOGIN;

        JSONObject jsonParams = new JSONObject();
        jsonParams.put(Constants.EMAIL, username);
        jsonParams.put(Constants.PASSWORD, password);

        StringEntity stringBody = new StringEntity(jsonParams.toString(), Charset.defaultCharset());
        stringBody.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()));

        final String[] token = {""};

        client.post(getApplicationContext(), url, stringBody, ContentType.APPLICATION_JSON.getMimeType(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                List<Header> listHeaders = new ArrayList<>(Arrays.asList(headers));
                for (Header header : listHeaders) {
                    if (header.getName().equals(Constants.HEADER_AUTHORIZATION))
                        token[0] =  header.getValue();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401)
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.stringWrongUserOrPassword), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });

        return token[0];
    }
}
