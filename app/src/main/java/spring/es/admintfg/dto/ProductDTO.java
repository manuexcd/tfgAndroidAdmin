package spring.es.admintfg.dto;

import java.io.Serializable;

public class ProductDTO implements Serializable {
	private static final long serialVersionUID = 4340552175235204140L;
	private long id;
	private String name;
	private String description;
	private double price;
	private int stockAvailable;
	private boolean isVisible = true;
	private ImageDTO productImage;

	public long getId() {
		return id;
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

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public ImageDTO getProductImage() {
		return productImage;
	}

	public void setProductImage(ImageDTO productImage) {
		this.productImage = productImage;
	}
}
