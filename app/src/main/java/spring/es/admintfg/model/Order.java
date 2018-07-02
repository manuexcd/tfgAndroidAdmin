package spring.es.admintfg.model;

import com.google.gson.Gson;

import java.sql.Date;
import java.util.Collection;

/**
 * Created by manuexcd on 7/07/17.
 */

public class Order {
    private enum OrderStatus {RECEIVED, IN_PROGRESS, IN_DELIVERY, DELIVERED}

    private long id;
    private double totalPrice;
    private Date date;
    private OrderStatus orderStatus;
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

    public Order(Date date, User user) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void updatePrice() {
        double total = 0;
        for (OrderLine orderLine : this.orderLines) {
            total += (orderLine.getProduct().getPrice() * orderLine.getQuantity());
        }
        this.setTotalPrice(total);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: " + this.getId() + ". Date: " + this.getDate().toString() + "\n");
        sb.append(this.getOrderLines().toString());

        return sb.toString();
    }
}