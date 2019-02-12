package spring.es.admintfg.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

import spring.es.admintfg.model.OrderLine;
import spring.es.admintfg.model.User;

public class OrderDTO implements Serializable {

	private enum OrderStatus {
		RECEIVED, IN_PROGRESS, IN_DELIVERY, DELIVERED
	}

	private static final long serialVersionUID = 6892693125355139371L;
	private long id;
	private double totalPrice = 0;
	private Timestamp date;
	private OrderStatus orderStatus = OrderStatus.RECEIVED;
	private Collection<OrderLine> orderLines;
	private User user;

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

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
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
