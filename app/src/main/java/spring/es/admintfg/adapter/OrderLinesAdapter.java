package spring.es.admintfg.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.MainActivity;
import spring.es.admintfg.activity.OrderDetailsActivity;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.OrderLineDTO;

/**
 * Created by manue on 07/04/2018.
 */

public class OrderLinesAdapter extends RecyclerView.Adapter<OrderLinesAdapter.ViewHolder> {
    private ArrayList<OrderLineDTO> ordersLines;
    private Context context;
    private MyApplication app;
    private static OrderDTO order;

    public OrderLinesAdapter(ArrayList<OrderLineDTO> ordersLines, Context context) {
        this.ordersLines = ordersLines;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderLinesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        app = (MyApplication) ((Activity) context).getApplication();

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_order_lines, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLinesAdapter.ViewHolder holder, int position) {
        OrderLineDTO currentOrderLine = ordersLines.get(position);
        holder.bindTo(currentOrderLine);
        GlideApp.with(context).load(currentOrderLine.getProduct().getProductImage().getUrl()).into(holder.orderLineProductImage);
    }

    public void setOrderLines(ArrayList<OrderLineDTO> ordersLines) {
        this.ordersLines = ordersLines;
    }

    @Override
    public int getItemCount() {
        if (ordersLines != null)
            return ordersLines.size();
        else
            return 0;
    }

    public void setOrder(OrderDTO order) {
        OrderLinesAdapter.order = order;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //Member Variables for the TextViews
        private ImageView orderLineProductImage;
        private TextView orderLineProductName;
        private TextView orderLineQuantity;
        private TextView orderLineProductPrice;
        private ImageButton deleteOrderLineBtn;
        private Long id;

        private void deleteOrderLine() {
            AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(context);
            String url = Constants.IP_ADDRESS + Constants.PATH_ORDERLINES + id;
            client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());

            client.delete(context, url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, OrderLinesAdapter.class));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                }
            });
        }

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
            deleteOrderLineBtn = itemView.findViewById(R.id.deleteOrderLineBtn);

            deleteOrderLineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrderLine();
                }
            });
        }

        void bindTo(OrderLineDTO currentOrderLine) {
            //Populate the textviews with data
            orderLineProductName.setText(currentOrderLine.getProduct().getName());
            orderLineQuantity.setText(String.valueOf(currentOrderLine.getQuantity()));
            orderLineProductPrice.setText(String.valueOf(new DecimalFormat("#.##").format(currentOrderLine.getProduct().getPrice())).concat(" ").concat(Constants.EURO));
            id = currentOrderLine.getId();

            if (app.isAdmin() || !OrderLinesAdapter.order.getOrderStatus().equals(Constants.ORDER_STATUS_TEMPORAL))
                deleteOrderLineBtn.setVisibility(View.INVISIBLE);
        }
    }
}
