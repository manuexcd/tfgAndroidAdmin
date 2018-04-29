package spring.es.admintfg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import spring.es.admintfg.GlideApp;
import spring.es.admintfg.R;
import spring.es.admintfg.model.User;

/**
 * Created by manue on 07/04/2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<User> users;
    private Context context;

    public UsersAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_users, parent, false));
    }

    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
        User currentUser = users.get(position);
        holder.bindTo(currentUser);
        GlideApp.with(context).load(currentUser.getUserImage().getUrl()).into(holder.userImage);
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
        private TextView address;
        private ImageView userImage;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file
         */
        ViewHolder(View itemView) {
            super(itemView);

            //Initialize the views
            fullName = itemView.findViewById(R.id.fullName);
            address = itemView.findViewById(R.id.address);
            userImage = itemView.findViewById(R.id.userImage);
        }

        void bindTo(User currentUser) {
            //Populate the textviews with data
            fullName.setText(currentUser.getName().concat(" ").concat(currentUser.getSurname()));
            address.setText(currentUser.getAddress());
        }
    }
}
