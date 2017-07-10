package spring.es.admintfg;

import com.google.gson.Gson;

/**
 * Created by manuexcd on 7/07/17.
 */

public class OrderLine {
    private long id;
    private int quantity;
    private Product product;
    private Order order;

    public static OrderLine fromJson(String s) {
        return new Gson().fromJson(s, OrderLine.class);
    }
    public String toJson() {
        return new Gson().toJson(this);
    }

    public OrderLine() {
    }

    public OrderLine(Product product, int quantity, Order order) {
        super();
        this.setProduct(product);
        this.setQuantity(quantity);
        this.setOrder(order);
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return this.order;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getProduct() + "\t x \t" + this.getQuantity());

        return sb.toString();
    }
}