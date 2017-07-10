package spring.es.admintfg;

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

    public static User fromJson(String s) {
        return new Gson().fromJson(s, User.class);
    }
    public String toJson() {
        return new Gson().toJson(this);
    }

    public Product() {
    }

    public Product(String name, String description, double price, int stockAvailable) {
        super();
        this.setName(name);
        this.setDescription(description);
        this.setPrice(price);
        this.setStockAvailable(stockAvailable);
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + ". Stock available: " + this.getStockAvailable() + ".\n");
        sb.append(this.getPrice() + " €\n");
        sb.append(this.getDescription());

        return sb.toString();
    }
}
