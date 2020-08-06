package spring.es.admintfg.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import de.cketti.mailto.EmailIntentBuilder;
import de.hdodenhof.circleimageview.CircleImageView;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.EditProfileActivity;
import spring.es.admintfg.activity.LoginActivity;
import spring.es.admintfg.dto.UserDTO;

public class ProfileFragment extends Fragment {
    private CircleImageView userDetailImage;
    private TextView userDetailName;
    private TextView userDetailPhone;
    private TextView userDetailEmail;
    private TextView userDetailAddress;
    private MyApplication app;

    public void getUserDetails() {
        AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(getActivity()).getApplicationContext());
        String url = Constants.IP_ADDRESS + Constants.PATH_USERS + app.getUserLogged().getId();
        client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                UserDTO user = gson.fromJson(new String(responseBody), UserDTO.class);
                if (user.getUserImage() != null) {
                    GlideApp.with(Objects.requireNonNull(getContext())).load(user.getUserImage()).into(userDetailImage);
                }
                userDetailName.setText(user.getName().concat(" ").concat(user.getSurname()));
                userDetailPhone.setText(user.getPhone());
                userDetailEmail.setText(user.getEmail());
                userDetailAddress.setText(user.getAddress());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getContext(), R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        app = MyApplication.getInstance();

        userDetailImage = view.findViewById(R.id.userDetailImage);
        userDetailName = view.findViewById(R.id.userDetailName);
        userDetailPhone = view.findViewById(R.id.userDetailPhone);
        ToggleButton btnUserDetailPhone = view.findViewById(R.id.btnUserDetailPhone);
        userDetailEmail = view.findViewById(R.id.userDetailEmail);
        ToggleButton btnUserDetailEmail = view.findViewById(R.id.btnUserDetailEmail);
        userDetailAddress = view.findViewById(R.id.userDetailAddress);
        ToggleButton btnUserDetailAddress = view.findViewById(R.id.btnUserDetailAddress);
        FloatingActionButton fabUserProfile = view.findViewById(R.id.fabUserProfile);

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
                Intent emailIntent = EmailIntentBuilder.from(view.getContext()).to(userDetailEmail.getText().toString()).build();
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

        fabUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), EditProfileActivity.class));
            }
        });

        return view;
    }
}
