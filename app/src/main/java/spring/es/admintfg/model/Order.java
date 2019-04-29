package spring.es.admintfg.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.Collection;

public class Order {
    private long id;
    private double totalPrice;
    private Timestamp date;
    private String orderStatus;
    private Collection<OrderLine> orderLines;
    private User user;

    public static Order fromJson(String s) {
        return new Gson().fromJson(s, Order.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public Order() {

    }

    public Order(Timestamp date, User user) {
        super();
        this.setDate(date);
        this.setTotalPrice(0);
        this.setUser(user);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<OrderLine> getOrderLines() {
        return this.orderLines;
    }

    public void setOrderLines(Collection<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void updatePrice() {
        double total = 0;
        for (OrderLine orderLine : this.orderLines) {
            total += (orderLine.getProduct().getPrice() * orderLine.getQuantity());
        }
        this.setTotalPrice(total);
    }

    @NonNull
    public String toString() {
        return "Order ID: " + this.getId() + ". Date: " + this.getDate().toString() + "\n" +
                this.getOrderLines().toString();
    }
}