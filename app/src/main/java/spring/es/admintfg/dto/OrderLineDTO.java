package spring.es.admintfg.dto;

import java.io.Serializable;

import spring.es.admintfg.model.Order;
import spring.es.admintfg.model.Product;

public class OrderLineDTO implements Serializable {
	private static final long serialVersionUID = 4745195410724554377L;
	private long id;
	private Product product;
	private int quantity;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
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

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	private Order order;
}
