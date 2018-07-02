package spring.es.admintfg.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.R;
import spring.es.admintfg.model.Order;
import spring.es.admintfg.model.OrderLine;

/**
 * Created by manue on 07/04/2018.
 */

public class OrderLinesAdapter extends RecyclerView.Adapter<OrderLinesAdapter.ViewHolder> {
    private ArrayList<OrderLine> ordersLines;
    private Context context;

    public OrderLinesAdapter(ArrayList<OrderLine> ordersLines, Context context) {
        this.ordersLines = ordersLines;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderLinesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_order_lines, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLinesAdapter.ViewHolder holder, int position) {
        OrderLine currentOrderLine = ordersLines.get(position);
        holder.bindTo(currentOrderLine);
        GlideApp.with(context).load(currentOrderLine.getProduct().getProductImage().getUrl()).into(holder.orderLineProductImage);
    }

    public void setOrderLines(ArrayList<OrderLine> ordersLines) {
        this.ordersLines = ordersLines;
    }

    @Override
    public int getItemCount() {
        if (ordersLines != null)
            return ordersLines.size();
        else
            return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //Member Variables for the TextViews
        private ImageView orderLineProductImage;
        private TextView orderLineProductName;
        private TextView orderLineQuantity;
        private TextView orderLineProductPrice;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file
         */
        ViewHolder(View itemView) {
            super(itemView);

            //Initialize the views
            orderLineProductImage = itemView.findViewById(R.id.orderLineProductImage);
            orderLineProductName = itemView.findViewById(R.id.orderLineProductName);
            orderLineQuantity = itemView.findViewById(R.id.orderLineQuantity);
            orderLineProductPrice = itemView.findViewById(R.id.orderLineProductPrice);
        }

        void bindTo(OrderLine currentOrderLine) {
            //Populate the textviews with data
            orderLineProductName.setText(currentOrderLine.getProduct().getName());
            orderLineQuantity.setText(String.valueOf(currentOrderLine.getQuantity()));
            orderLineProductPrice.setText(String.valueOf(new DecimalFormat("#.##").format(currentOrderLine.getProduct().getPrice())).concat(" ").concat(Constants.EURO));
        }
    }
}
