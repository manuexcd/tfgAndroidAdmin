package spring.es.admintfg.dto;

import com.google.gson.Gson;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

import spring.es.admintfg.model.OrderLine;
import spring.es.admintfg.model.User;

public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 6892693125355139371L;
    private long id;
    private double totalPrice = 0;
    private Timestamp date;
    private String orderStatus;
    private Collection<OrderLine> orderLines;
    private User user;

    public static OrderDTO fromJson(String s) {
        return new Gson().fromJson(s, OrderDTO.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Collection<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Collection<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
