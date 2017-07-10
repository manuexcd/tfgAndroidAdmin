package spring.es.admintfg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class UsersActivity extends AppCompatActivity {

    private static ArrayList<User> usersArray;
    private static ArrayAdapter<User> usersAdapter;
    private static ListView usersList;
    private static String ip = "http://192.168.1.28:8080/";

    public void getUsers() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = ip + "users";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Gson gson = new Gson();
                Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                User[] users = gson.fromJson(new String(responseBody), User[].class);
                usersArray = new ArrayList<User>(Arrays.asList(users));
                usersAdapter = new ArrayAdapter<User>(UsersActivity.this, android.R.layout.simple_list_item_activated_1, usersArray);
                usersList.setAdapter(usersAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = (ListView)findViewById(R.id.usersList);

        getUsers();
    }
}
