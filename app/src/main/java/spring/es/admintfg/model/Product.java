package spring.es.admintfg.model;

import com.google.gson.Gson;

/**
 * Created by manuexcd on 7/07/17.
 */

public class Product {
    private long id;
    private String name;
    private String description;
    private double price;
    private int stockAvaiable;
    private Image productImage;

    public static Product fromJson(String s) {
        return new Gson().fromJson(s, Product.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public Product() {
    }

    public Product(String name, String description, double price, int stockAvaiable, Image productImage) {
        super();
        this.setName(name);
        this.setDescription(description);
        this.setPrice(price);
        this.setStockAvaiable(stockAvaiable);
        this.setProductImage(productImage);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockAvaiable() {
        return stockAvaiable;
    }

    public void setStockAvaiable(int stockAvaiable) {
        this.stockAvaiable = stockAvaiable;
    }

    public void updateStock(int stock) {
        this.stockAvaiable -= stock;
    }

    public Image getProductImage() {
        return this.productImage;
    }

    public void setProductImage(Image productImage) {
        this.productImage = productImage;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + ". Stock avaiable: " + this.getStockAvaiable() + ".\n");
        sb.append(this.getPrice() + " €\n");
        sb.append(this.getDescription());

        return sb.toString();
    }
}