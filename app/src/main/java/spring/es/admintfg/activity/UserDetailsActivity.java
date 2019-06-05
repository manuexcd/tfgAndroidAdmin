package spring.es.admintfg.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.cketti.mailto.EmailIntentBuilder;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.adapter.OrdersAdapter;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.UserDTO;
import spring.es.admintfg.pagination.OrdersPage;

public class UserDetailsActivity extends AppCompatActivity {
    private ArrayList<OrderDTO> ordersArray;
    private ImageView userDetailImage;
    private TextView userDetailPhone;
    private TextView userDetailEmail;
    private TextView userDetailAddress;
    private Toolbar toolbar;
    private OrdersAdapter mAdapter;
    private MyApplication app;

    public void getUserDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + getIntent().getStringExtra(Constants.USER_ID);
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                UserDTO user = gson.fromJson(new String(responseBody), UserDTO.class);
                if (user.getUserImage() != null) {
                    GlideApp.with(getApplicationContext()).load(user.getUserImage().getUrl()).dontAnimate().into(userDetailImage);
                }
                toolbar.setTitle(user.getName().concat(" ").concat(user.getSurname()));
                toolbar.setTitleTextColor(getColor(R.color.white));
                setSupportActionBar(toolbar);
                userDetailPhone.setText(user.getPhone());
                userDetailEmail.setText(user.getEmail());
                userDetailAddress.setText(user.getAddress());
                getOrdersByUser(user.getId());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(UserDetailsActivity.this, LoginActivity.class));
            }
        });
    }

    public void getOrdersByUser(long userId) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + userId + "/" + Constants.PATH_ORDERS;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                OrdersPage orders = gson.fromJson(new String(responseBody), OrdersPage.class);
                ordersArray = new ArrayList<>(orders.getContent());
                mAdapter.setOrders(ordersArray);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(UserDetailsActivity.this, LoginActivity.class));
                Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        toolbar = findViewById(R.id.toolbarUserDetails);

        app = (MyApplication) this.getApplication();

        userDetailImage = findViewById(R.id.userDetailImage);
        userDetailPhone = findViewById(R.id.userDetailPhone);
        ToggleButton btnUserDetailPhone = findViewById(R.id.btnUserDetailPhone);
        userDetailEmail = findViewById(R.id.userDetailEmail);
        ToggleButton btnUserDetailEmail = findViewById(R.id.btnUserDetailEmail);
        userDetailAddress = findViewById(R.id.userDetailAddress);
        ToggleButton btnUserDetailAddress = findViewById(R.id.btnUserDetailAddress);

        RecyclerView ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new OrdersAdapter(ordersArray, this);
        ordersRecyclerView.setAdapter(mAdapter);

        getUserDetails();

        btnUserDetailPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = userDetailPhone.getText().toString();
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", phone, null));
                startActivity(phoneIntent);
            }
        });

        btnUserDetailEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = EmailIntentBuilder.from(getApplicationContext()).to(userDetailEmail.getText().toString()).build();
                startActivity(emailIntent);
            }
        });

        btnUserDetailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = userDetailAddress.getText().toString();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
                Intent addressIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                addressIntent.setPackage("com.google.android.apps.maps");
                startActivity(addressIntent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabUserDetails);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
