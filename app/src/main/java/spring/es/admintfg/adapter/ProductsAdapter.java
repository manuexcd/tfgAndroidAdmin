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
import java.util.List;

import spring.es.admintfg.Constants;
import spring.es.admintfg.GlideApp;
import spring.es.admintfg.R;
import spring.es.admintfg.activity.ProductDetailsActivity;
import spring.es.admintfg.dto.ProductDTO;

/**
 * Created by manue on 07/04/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private ArrayList<ProductDTO> products;
    private Context context;

    public ProductsAdapter(ArrayList<ProductDTO> products, Context context) {
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
        ProductDTO currentProduct = products.get(position);
        holder.bindTo(currentProduct);
        if (currentProduct.getProductImage() != null)
            GlideApp.with(context).load(currentProduct.getProductImage().getUrl()).into(holder.productImage);
    }

    public List<ProductDTO> getProducts() {
        return this.products;
    }

    public void setProducts(ArrayList<ProductDTO> products) {
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

        void bindTo(ProductDTO currentProduct) {
            //Populate the textviews with data
            productName.setText(currentProduct.getName());
            productPrice.setText(String.valueOf(currentProduct.getPrice()).concat(" ").concat(Constants.EURO));
        }

        @Override
        public void onClick(View v) {
            ProductDTO currentProduct = products.get(getAdapterPosition());
            Intent intent = ((Activity) context).getIntent();

            Intent detailIntent = new Intent(context, ProductDetailsActivity.class);
            detailIntent.putExtra(Constants.PRODUCT_ID, String.valueOf(currentProduct.getId()));
            detailIntent.putExtra(Constants.TOKEN, intent.getStringExtra(Constants.TOKEN));
            detailIntent.putExtra(Constants.HEADER_ADMIN, intent.getStringExtra(Constants.HEADER_ADMIN));
            context.startActivity(detailIntent);
        }
    }
}
