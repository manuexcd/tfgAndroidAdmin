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
    private int stockAvailable;
    private boolean isVisible;
    private Image productImage;

    public Product() {
    }

    public Product(String name, String description, double price, int stockAvailable, Image productImage) {
        super();
        this.setName(name);
        this.setDescription(description);
        this.setPrice(price);
        this.setStockAvailable(stockAvailable);
        this.setProductImage(productImage);
    }

    public static Product fromJson(String s) {
        return new Gson().fromJson(s, Product.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
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

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public void updateStock(int stock) {
        this.stockAvailable -= stock;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Image getProductImage() {
        return this.productImage;
    }

    public void setProductImage(Image productImage) {
        this.productImage = productImage;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(". Stock available: ").append(this.getStockAvailable()).append(".\n");
        sb.append(this.getPrice()).append(" €\n");
        sb.append(this.getDescription());

        return sb.toString();
    }
}