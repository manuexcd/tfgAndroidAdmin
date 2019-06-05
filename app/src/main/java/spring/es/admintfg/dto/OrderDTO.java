package spring.es.admintfg.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 6892693125355139371L;
    private long id;
    private double totalPrice;
    private Timestamp date;
    @JsonSerialize(using = CustomDateSerializer.class)
    private String orderStatus;
    private List<OrderLineDTO> orderLines;

    private UserDTO user;

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

    public List<OrderLineDTO> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLineDTO> orderLines) {
        this.orderLines = orderLines;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
