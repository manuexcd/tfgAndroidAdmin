package spring.es.admintfg.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.R;
import spring.es.admintfg.adapter.UsersAdapter;
import spring.es.admintfg.model.User;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private UsersAdapter mAdapter;
    private static ArrayList<User> usersArray;
    private static ArrayAdapter<User> usersAdapter;

    public void getUsers() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + "users";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                User[] users = gson.fromJson(new String(responseBody), User[].class);
                usersArray = new ArrayList<User>(Arrays.asList(users));
                usersAdapter = new ArrayAdapter<User>(UsersActivity.this, android.R.layout.simple_list_item_activated_1, usersArray);
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

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getUsers();

        mAdapter = new UsersAdapter(usersArray, this);
        usersRecyclerView.setAdapter(mAdapter);
    }
}
