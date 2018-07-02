package spring.es.admintfg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.R;
import spring.es.admintfg.RecyclerItemClickListener;
import spring.es.admintfg.activity.UserDetailsActivity;
import spring.es.admintfg.adapter.UsersAdapter;
import spring.es.admintfg.model.User;

public class UsersFragment extends Fragment {
    private static ArrayList<User> usersArray;
    private UsersAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText editTextSearchUser;

    public void getUsers() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + "users";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                User[] users = gson.fromJson(new String(responseBody), User[].class);
                usersArray = new ArrayList<>(Arrays.asList(users));
                mAdapter.setUsers(usersArray);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                editTextSearchUser.setHint(String.valueOf(usersArray.size()).concat(" contactos."));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUsersByParam(String param) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + "search/" + param;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                User[] users = gson.fromJson(new String(responseBody), User[].class);
                usersArray = new ArrayList<>(Arrays.asList(users));
                mAdapter.setUsers(usersArray);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_users, container, false);

        RecyclerView usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAdapter = new UsersAdapter(usersArray, view.getContext());
        usersRecyclerView.setAdapter(mAdapter);

        getUsers();

        usersRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), usersRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User currentUser = usersArray.get(position);

                        Intent detailIntent = new Intent(view.getContext(), UserDetailsActivity.class);
                        detailIntent.putExtra("userId", String.valueOf(currentUser.getId()));
                        view.getContext().startActivity(detailIntent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshUsers);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getUsers();
                    }
                }
        );

        editTextSearchUser = view.findViewById(R.id.editTextSearchUser);

        editTextSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(""))
                    getUsersByParam(s.toString());
                else
                    getUsers();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
}
