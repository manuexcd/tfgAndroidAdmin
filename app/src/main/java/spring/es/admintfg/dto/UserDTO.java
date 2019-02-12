package spring.es.admintfg.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import spring.es.admintfg.model.Image;
import spring.es.admintfg.model.Order;

public class UserDTO implements Serializable {

	private static final long serialVersionUID = 7110275440135292814L;
	private long id;
	private String name;
	private String surname;
	private String address;
	private String phone;
	private String email;
	private String password;
	private Collection<Order> orders;
	private Image userImage;
	private List<String> roles;

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

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Order> getOrders() {
		return orders;
	}

	public void setOrders(Collection<Order> orders) {
		this.orders = orders;
	}

	public Image getUserImage() {
		return userImage;
	}

	public void setUserImage(Image userImage) {
		this.userImage = userImage;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
