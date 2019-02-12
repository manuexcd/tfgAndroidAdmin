package spring.es.admintfg.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.PaginationScrollListener;
import spring.es.admintfg.R;
import spring.es.admintfg.RecyclerItemClickListener;
import spring.es.admintfg.activity.UserDetailsActivity;
import spring.es.admintfg.adapter.UsersAdapter;
import spring.es.admintfg.dto.UserDTO;
import spring.es.admintfg.pagination.UsersPage;

public class UsersFragment extends Fragment {
    private static ArrayList<UserDTO> usersArray;
    private UsersAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText editTextSearchUser;
    private ProgressBar progressBar;

    private static final int PAGE_START = 0;
    private static boolean isLoading = false;
    private static boolean isLastPage = false;
    private static int TOTAL_PAGES;
    private static int currentPage = PAGE_START;

    public void setUsersList(byte[] responseBody) {
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATETIME_FORMAT).setPrettyPrinting().create();
        UsersPage usersPage = gson.fromJson(new String(responseBody), UsersPage.class);
        usersArray = new ArrayList<>(usersPage.getContent());
        TOTAL_PAGES = usersPage.getTotalPages();
        if (usersPage.getFirst())
            mAdapter.setUsers(usersArray);
        else if (!isLastPage)
            mAdapter.getUsers().addAll(usersArray);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        editTextSearchUser.setHint(String.valueOf(usersPage.getTotalElements()).concat(" usuarios."));
        TOTAL_PAGES = usersPage.getTotalPages();
        isLoading = false;
        progressBar.setVisibility(View.GONE);
        isLastPage = usersPage.getLast();
    }

    public void getUsers(final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + Constants.PARAM_PAGE + page;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getActivity().getIntent().getStringExtra(Constants.TOKEN));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                isLoading = true;
                setUsersList(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), String.valueOf(statusCode), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUsersByParam(String param, final int page) {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + Constants.PATH_SEARCH + param + Constants.PARAM_PAGE + page;
        client.addHeader(Constants.HEADER_AUTHORIZATION, getActivity().getIntent().getStringExtra(Constants.TOKEN));
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setUsersList(responseBody);
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
        progressBar = view.findViewById(R.id.main_progress);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        usersRecyclerView.setLayoutManager(layoutManager);
        usersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new UsersAdapter(usersArray, view.getContext());
        usersRecyclerView.setAdapter(mAdapter);

        getUsers(PAGE_START);

        usersRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), usersRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        UserDTO currentUser = mAdapter.getUsers().get(position);

                        Intent detailIntent = new Intent(view.getContext(), UserDetailsActivity.class);
                        detailIntent.putExtra(Constants.USER_ID, String.valueOf(currentUser.getId()));
                        detailIntent.putExtra(Constants.TOKEN, Objects.requireNonNull(getActivity()).getIntent().getStringExtra(Constants.TOKEN));
                        view.getContext().startActivity(detailIntent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        usersRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                progressBar.setVisibility(View.VISIBLE);
                currentPage += 1;
                getUsers(currentPage);
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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshUsers);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        isLastPage = false;
                        currentPage = 0;
                        getUsers(PAGE_START);
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
                if (!s.toString().equals(Constants.EMPTY_STRING)) {
                    getUsersByParam(s.toString(), PAGE_START);
                } else {
                    getUsers(PAGE_START);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
}
