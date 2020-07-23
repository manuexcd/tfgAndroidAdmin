package spring.es.admintfg.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import spring.es.admintfg.Constants;
import spring.es.admintfg.R;
import spring.es.admintfg.dto.OrderDTO;

/**
 * Created by manue on 07/04/2018.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private ArrayList<OrderDTO> orders;
    private Context context;

    public OrdersAdapter(ArrayList<OrderDTO> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        OrderDTO currentOrder = orders.get(position);
        holder.bindTo(currentOrder);
    }

    public void setOrders(ArrayList<OrderDTO> orders) {
        this.orders = orders;
    }

    @Override
    public int getItemCount() {
        if (orders != null)
            return orders.size();
        else
            return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //Member Variables for the TextViews
        private TextView orderId;
        private TextView orderDate;
        private TextView orderPrice;
        private TextView orderUser;
        private TextView orderStatus;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file
         */
        ViewHolder(View itemView) {
            super(itemView);

            //Initialize the views
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderUser = itemView.findViewById(R.id.orderUser);
            orderStatus = itemView.findViewById(R.id.orderStatus);
        }

        void bindTo(OrderDTO currentOrder) {
            //Populate the textviews with data
            orderId.setText(("ID: ").concat(String.valueOf(currentOrder.getId())));
            orderDate.setText(currentOrder.getDate().toString());
            orderPrice.setText(String.valueOf(new DecimalFormat("#.##").format(currentOrder.getTotalPrice())).concat(" €"));
            orderStatus.setText(currentOrder.getOrderStatus());
            //orderUser.setText(currentOrder.getUser().getName().concat(" ").concat(currentOrder.getUser().getSurname()));
        }
    }
}
