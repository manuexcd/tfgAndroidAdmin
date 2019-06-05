package spring.es.admintfg.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.ImageDTO;
import spring.es.admintfg.dto.UserDTO;

/**
 * Created by manue on 07/04/2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<UserDTO> users;
    private Context context;
    private MyApplication app;

    public UsersAdapter(ArrayList<UserDTO> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        app = (MyApplication) ((Activity) context).getApplication();

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersAdapter.ViewHolder holder, int position) {
        UserDTO currentUser = users.get(position);

        holder.bindTo(currentUser);
    }

    public void setUsers(ArrayList<UserDTO> users) {
        this.users = users;
    }

    public List<UserDTO> getUsers() {
        return this.users;
    }

    @Override
    public int getItemCount() {
        if (users != null)
            return users.size();
        else
            return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //Member Variables for the TextViews
        private TextView fullName;
        private CircleImageView userImage;
        private TextView address;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file
         */
        ViewHolder(View itemView) {
            super(itemView);

            //Initialize the views
            fullName = itemView.findViewById(R.id.fullName);
            userImage = itemView.findViewById(R.id.userImage);
            address = itemView.findViewById(R.id.address);
        }

        void bindTo(UserDTO currentUser) {
            //Populate the textviews with data
            fullName.setText(currentUser.getName().concat(" ").concat(currentUser.getSurname()));
            GlideApp.with(context).load(currentUser.getUserImage().getUrl()).dontAnimate().into(userImage);
            address.setText(currentUser.getAddress());
        }
    }
}
