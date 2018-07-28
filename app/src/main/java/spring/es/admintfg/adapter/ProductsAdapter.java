package spring.es.admintfg.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.ProductDetailsActivity;
import spring.es.admintfg.model.Product;

/**
 * Created by manue on 07/04/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private ArrayList<Product> products;
    private Context context;

    public ProductsAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @Override
    @NonNull
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int position) {
        Product currentProduct = products.get(position);
        holder.bindTo(currentProduct);
        GlideApp.with(context).load(currentProduct.getProductImage().getUrl()).into(holder.productImage);
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    @Override
    public int getItemCount() {
        if (products != null)
            return products.size();
        else
            return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Member Variables for the TextViews
        private TextView productName;
        private TextView productPrice;
        private ImageView productImage;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file
         */
        ViewHolder(View itemView) {
            super(itemView);

            //Initialize the views
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);

            itemView.setOnClickListener(this);
        }

        void bindTo(Product currentProduct) {
            //Populate the textviews with data
            productName.setText(currentProduct.getName());
            productPrice.setText(String.valueOf(currentProduct.getPrice()).concat(" ").concat(Constants.EURO));
        }

        @Override
        public void onClick(View v) {
            Product currentProduct = products.get(getAdapterPosition());
            Intent intent = ((Activity) context).getIntent();

            Intent detailIntent = new Intent(context, ProductDetailsActivity.class);
            detailIntent.putExtra("productId", String.valueOf(currentProduct.getId()));
            detailIntent.putExtra("token", intent.getStringExtra("token"));
            context.startActivity(detailIntent);
        }
    }
}
