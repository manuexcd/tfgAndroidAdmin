package spring.es.admintfg.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.UserDetailsActivity;
import spring.es.admintfg.dto.UserDTO;

/**
 * Created by manue on 07/04/2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<UserDTO> users;
    private Context context;

    public UsersAdapter(ArrayList<UserDTO> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView fullName;
        private CircleImageView userImage;
        private TextView address;

        ViewHolder(View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.fullName);
            userImage = itemView.findViewById(R.id.userImage);
            address = itemView.findViewById(R.id.address);

            itemView.setOnClickListener(this);
        }

        void bindTo(UserDTO currentUser) {
            fullName.setText(currentUser.getName().concat(" ").concat(currentUser.getSurname()));
            if (currentUser.getUserImage() != null)
                GlideApp.with(context).load(currentUser.getUserImage()).dontAnimate().into(userImage);
            address.setText(currentUser.getAddress());
        }

        @Override
        public void onClick(View v) {
            UserDTO currentUser = users.get(getAdapterPosition());

            Intent detailIntent = new Intent(context, UserDetailsActivity.class);
            detailIntent.putExtra(Constants.USER_ID, String.valueOf(currentUser.getId()));
            context.startActivity(detailIntent);
        }
    }
}
