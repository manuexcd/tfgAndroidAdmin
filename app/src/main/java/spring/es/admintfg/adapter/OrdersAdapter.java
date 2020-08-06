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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import spring.es.admintfg.Constants;
import spring.es.admintfg.MyApplication;
import spring.es.admintfg.MyAsyncHttpClient;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.LoginActivity;
import spring.es.admintfg.activity.OrderDetailsActivity;
import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.OrderLineDTO;
import spring.es.admintfg.dto.ProductDTO;
import spring.es.admintfg.dto.UserDTO;

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

    public List<OrderDTO> getOrders() {
        return this.orders;
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView orderId;
        private TextView orderDate;
        private TextView orderPrice;
        private TextView orderProducts;
        private TextView orderStatus;
        private TextView orderUser;
        private MyApplication app;
        private List<ProductDTO> products = new ArrayList<>();

        public void getProductDetails(long productId) {
            AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(context);
            String url = Constants.IP_ADDRESS + Constants.PATH_PRODUCTS + productId;
            client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                    products.add(gson.fromJson(new String(responseBody), ProductDTO.class));
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

        public void getUserDetails(long userId) {
            AsyncHttpClient client = MyAsyncHttpClient.getAsyncHttpClient(Objects.requireNonNull(context));
            String url = Constants.IP_ADDRESS + Constants.PATH_USERS + userId;
            client.addHeader(Constants.HEADER_AUTHORIZATION, app.getToken());
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create();
                    UserDTO user = gson.fromJson(new String(responseBody), UserDTO.class);
                    orderUser.setText(user.getName().concat(" ").concat(user.getSurname()));
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

        ViewHolder(View itemView) {
            super(itemView);

            app = MyApplication.getInstance();

            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderProducts = itemView.findViewById(R.id.orderProducts);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderUser = itemView.findViewById(R.id.orderUser);

            itemView.setOnClickListener(this);
        }

        void bindTo(OrderDTO currentOrder) {
            orderId.setText((String.valueOf(currentOrder.getId())));
            orderDate.setText(DateFormat.getDateInstance().format(currentOrder.getDate()));
            orderPrice.setText(String.valueOf(new DecimalFormat("#.##").format(currentOrder.getTotalPrice())).concat(Constants.EURO));
            orderStatus.setText(currentOrder.getOrderStatus());

            switch (currentOrder.getOrderStatus()) {
                case Constants.ORDER_STATUS_TEMPORAL:
                    orderStatus.setText(R.string.stringOrderTemporal);
                    break;
                case Constants.ORDER_STATUS_RECEIVED:
                    orderStatus.setText(R.string.stringOrderReceived);
                    break;
                case Constants.ORDER_STATUS_IN_PROGRESS:
                    orderStatus.setText(R.string.stringOrderInProgress);
                    break;
                case Constants.ORDER_STATUS_IN_DELIVERY:
                    orderStatus.setText(R.string.stringOrderInDelivery);
                    break;
                case Constants.ORDER_STATUS_CANCELLED:
                    orderStatus.setText(R.string.stringOrderCancelled);
                    break;
                case Constants.ORDER_STATUS_DELIVERED:
                    orderStatus.setText(R.string.stringOrderDelivered);
                    break;
            }

            getUserDetails(currentOrder.getUserId());
            List<String> listProductNames = new ArrayList<>();
            for (OrderLineDTO line : currentOrder.getOrderLines()) {
                getProductDetails(line.getProductId());
            }
            for (int i = 0; i < products.size(); i++) {
                if (!listProductNames.contains(products.get(i).getName()))
                    listProductNames.add(products.get(i).getName());
            }

            orderProducts.setText("");
            orderProducts.setText(android.text.TextUtils.join(", ", listProductNames));
        }

        @Override
        public void onClick(View v) {
            OrderDTO currentOrder = orders.get(getAdapterPosition());
            Intent intent = ((Activity) context).getIntent();

            Intent detailIntent = new Intent(context, OrderDetailsActivity.class);
            detailIntent.putExtra(Constants.ORDER_ID, String.valueOf(currentOrder.getId()));
            detailIntent.putExtra(Constants.TOKEN, intent.getStringExtra(Constants.TOKEN));
            detailIntent.putExtra(Constants.HEADER_ADMIN, intent.getStringExtra(Constants.HEADER_ADMIN));
            detailIntent.putExtra(Constants.USER_ID, intent.getStringExtra(Constants.USER_ID));
            context.startActivity(detailIntent);
        }
    }
}
