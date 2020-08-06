package spring.es.admintfg.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
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
import de.hdodenhof.circleimageview.CircleImageView;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.PaginationScrollListener;
import spring.es.admintfg.R;
import spring.es.admintfg.adapter.OrdersAdapter;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.UserDTO;
import spring.es.admintfg.pagination.OrdersPage;

public class UserDetailsActivity extends AppCompatActivity {
    private ArrayList<OrderDTO> ordersArray;
    private CircleImageView userDetailImage;
    private TextView userDetailFullname;
    private TextView userDetailPhone;
    private TextView userDetailEmail;
    private TextView userDetailAddress;
    private OrdersAdapter mAdapter;
    private MyApplication app;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

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
                    GlideApp.with(getApplicationContext()).load(user.getUserImage()).dontAnimate().into(userDetailImage);
                }
                userDetailFullname.setText(user.getName().concat(" ").concat(user.getSurname()));
                userDetailPhone.setText(user.getPhone());
                userDetailEmail.setText(user.getEmail());
                userDetailAddress.setText(user.getAddress());
                getOrdersByDateDesc(user.getId(), PAGE_START);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(UserDetailsActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void setOrdersList(byte[] responseBody) {
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).create();
        OrdersPage ordersPage = gson.fromJson(new String(responseBody), OrdersPage.class);
        ordersArray = new ArrayList<>(ordersPage.getContent());
        TOTAL_PAGES = ordersPage.getTotalPages();
        if (ordersPage.getFirst())
            mAdapter.setOrders(ordersArray);
        else if (!isLastPage)
            mAdapter.getOrders().addAll(ordersArray);
        mAdapter.notifyDataSetChanged();
        TOTAL_PAGES = ordersPage.getTotalPages();
        isLastPage = ordersPage.getLast();
    }

    public void getOrdersByDateDesc(long userId, final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PATH_DATE_DESC + Constants.PARAM_USER_ID + userId + "&page=" + page;

        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setOrdersList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(UserDetailsActivity.this, LoginActivity.class));
                    Toast.makeText(getApplicationContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        app = (MyApplication) this.getApplication();

        Toolbar toolbar = findViewById(R.id.toolbarUserDetails);
        toolbar.setTitle("Perfil de usuario");
        toolbar.setTitleMarginStart(150);
        toolbar.setTitleTextColor(getColor(R.color.white));

        userDetailImage = findViewById(R.id.userDetailImage);
        userDetailFullname = findViewById(R.id.userDetailFullname);
        userDetailPhone = findViewById(R.id.userDetailPhone);
        ToggleButton btnUserDetailPhone = findViewById(R.id.btnUserDetailPhone);
        userDetailEmail = findViewById(R.id.userDetailEmail);
        ToggleButton btnUserDetailEmail = findViewById(R.id.btnUserDetailEmail);
        userDetailAddress = findViewById(R.id.userDetailAddress);
        ToggleButton btnUserDetailAddress = findViewById(R.id.btnUserDetailAddress);

        RecyclerView ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ordersRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new OrdersAdapter(ordersArray, this);
        ordersRecyclerView.setAdapter(mAdapter);

        ordersRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                getOrdersByDateDesc(Long.parseLong(getIntent().getStringExtra(Constants.USER_ID)), currentPage);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        getUserDetails();

        final ImageButton productDetailBackBtn = findViewById(R.id.userDetailBackBtn);
        productDetailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
    }
}
