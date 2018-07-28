package spring.es.admintfg.model;

import com.google.gson.Gson;

import java.util.Collection;

/**
 * Created by manuexcd on 7/07/17.
 */

public class User {

    private long id;
    private String name;
    private String surname;
    private String address;
    private String phone;
    private String email;
    private Collection<Order> orders;
    private Image userImage;
    private String password;

    public static User fromJson(String s) {
        return new Gson().fromJson(s, User.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public User() {

    }

    public User(String name, String surname, String address, String phone, String email, Collection<Order> orders, Image userImage) {
        super();
        this.setName(name);
        this.setSurname(surname);
        this.setAddress(address);
        this.setPhone(phone);
        this.setEmail(email);
        this.setOrders(orders);
        this.setUserImage(userImage);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private void setOrders(Collection<Order> orders) {
        this.orders = orders;
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

    public Image getUserImage() {
        return this.userImage;
    }

    public void setUserImage(Image userImage) {
        this.userImage = userImage;
    }

    public String getFullName() {
        return this.name.concat(" ").concat(this.surname);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public String toString() {
        return this.getName() + " " + this.getSurname() + ".\n" + this.getAddress() + ".\n" + this.getEmail() + "\n" + this.getPhone();
    }
}