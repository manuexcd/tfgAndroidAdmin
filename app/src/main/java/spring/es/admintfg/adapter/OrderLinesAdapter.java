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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.LoginActivity;
import spring.es.admintfg.activity.MainActivity;
import spring.es.admintfg.activity.OrderDetailsActivity;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.OrderLineDTO;
import spring.es.admintfg.dto.ProductDTO;

/**
 * Created by manue on 07/04/2018.
 */

public class OrderLinesAdapter extends RecyclerView.Adapter<OrderLinesAdapter.ViewHolder> {
    private ArrayList<OrderLineDTO> ordersLines;
    private Context context;
    private MyApplication app;
    private static OrderDTO order;
    private OrderLineDTO lineToRemove;

    public OrderLinesAdapter(ArrayList<OrderLineDTO> ordersLines, Context context) {
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
        OrderLineDTO currentOrderLine = ordersLines.get(position);
        holder.bindTo(currentOrderLine);
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
        private CircleImageView orderLineProductImage;
        private TextView orderLineProductName;
        private TextView orderLineQuantity;
        private TextView orderLineProductPrice;
        private TextView orderLinePrice;
        private ImageButton deleteOrderLineBtn;
        private Long id;

        private void getProductDetails(final OrderLineDTO currentOrderLine) {
            AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(context);
            String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + currentOrderLine.getProductId();
            client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                    ProductDTO product = gson.fromJson(new String(responseBody), ProductDTO.class);
                    orderLineProductName.setText(product.getName());
                    orderLineProductPrice.setText(String.valueOf(new DecimalFormat("#.##").format(product.getPrice())).concat(Constants.EURO));
                    if (product.getProductImage() != null && !product.getProductImage().equals("")) {
                        GlideApp.with(context).load(product.getProductImage()).dontAnimate().into(orderLineProductImage);
                    }
                    orderLinePrice.setText(String.valueOf(new DecimalFormat("#.##").format(currentOrderLine.getQuantity() * product.getPrice())).concat(Constants.EURO));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                        Toast.makeText(context, R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void deleteOrderLine() {
            AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(context);
            String url = Constants.IP_ADDRESS + Constants.PATH_ORDER_LINES + id;
            client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());

            client.delete(context, url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    ordersLines.remove(lineToRemove);
                    OrderLinesAdapter.super.notifyDataSetChanged();

                    if(!ordersLines.isEmpty()) {
                        Intent intent = ((Activity) context).getIntent();

                        Intent detailIntent = new Intent(context, OrderDetailsActivity.class);
                        detailIntent.putExtra(Constants.ORDER_ID, String.valueOf(order.getId()));
                        detailIntent.putExtra(Constants.TOKEN, intent.getStringExtra(Constants.TOKEN));
                        detailIntent.putExtra(Constants.HEADER_ADMIN, intent.getStringExtra(Constants.HEADER_ADMIN));
                        detailIntent.putExtra(Constants.USER_ID, intent.getStringExtra(Constants.USER_ID));
                        context.startActivity(detailIntent);
                    }
                    else
                        context.startActivity(new Intent(context, MainActivity.class));
                    ((Activity)context).finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 500 && new String(responseBody).contains(Constants.EXPIRED)) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                        Toast.makeText(context, R.string.stringTokenExpired, Toast.LENGTH_LONG).show();
                    }
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

            app = MyApplication.getInstance();

            orderLineProductImage = itemView.findViewById(R.id.orderLineProductImage);
            orderLineProductName = itemView.findViewById(R.id.orderLineProductName);
            orderLineQuantity = itemView.findViewById(R.id.orderLineQuantity);
            orderLineProductPrice = itemView.findViewById(R.id.orderLineProductPrice);
            orderLinePrice = itemView.findViewById(R.id.orderLinePrice);
            deleteOrderLineBtn = itemView.findViewById(R.id.deleteOrderLineBtn);

            deleteOrderLineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrderLine();
                }
            });
        }

        void bindTo(OrderLineDTO currentOrderLine) {
            getProductDetails(currentOrderLine);
            lineToRemove = currentOrderLine;
            orderLineQuantity.setText(String.valueOf(currentOrderLine.getQuantity()));
            id = currentOrderLine.getId();
            if (app.isAdmin() || !OrderLinesAdapter.order.getOrderStatus().equals(Constants.ORDER_STATUS_TEMPORAL))
                deleteOrderLineBtn.setVisibility(View.INVISIBLE);
        }
    }
}
