package spring.es.admintfg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.adapter.PagerAdapter;
import spring.es.admintfg.dto.OrderDTO;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ImageButton cartButton;
    private Long orderId;
    private MyApplication app;

    public void getTemporalOrder() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_ORDERS + Constants.PATH_TEMPORAL;
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode != HttpStatus.SC_NO_CONTENT) {
                    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                    cartButton.setColorFilter(Color.RED);
                    orderId = gson.fromJson(new String(responseBody), OrderDTO.class).getId();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                if(statusCode == 500 && response.contains("expired"))
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                Toast.makeText(getApplicationContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (MyApplication) this.getApplication();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderId != null) {
                    Intent changeActivity = new Intent(MainActivity.this, OrderDetailsActivity.class);
                    changeActivity.putExtra(Constants.TOKEN, getIntent().getStringExtra(Constants.TOKEN));
                    changeActivity.putExtra(Constants.ORDER_ID, String.valueOf(orderId));
                    startActivity(changeActivity);
                }
            }
        });

        tabLayout = findViewById(R.id.tabs);
    }

    @Override
    protected void onStart() {
        if (tabLayout.getTabCount() == 0) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.stringProducts));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.stringOrders));
            if (app.isAdmin())
                tabLayout.addTab(tabLayout.newTab().setText(R.string.stringUsers));
            else
                getTemporalOrder();
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = findViewById(R.id.viewpager);
            final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        super.onStart();
    }
}