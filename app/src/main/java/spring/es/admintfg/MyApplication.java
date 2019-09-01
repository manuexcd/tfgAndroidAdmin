package spring.es.admintfg;

import android.app.Application;
import android.content.Context;

import spring.es.admintfg.dto.OrderDTO;
import spring.es.admintfg.dto.UserDTO;

public class MyApplication extends Application {

    private String token;
    private Boolean isAdmin = false;
    private UserDTO userLogged;
    private OrderDTO temporalOrder;

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public UserDTO getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(UserDTO userLogged) {
        this.userLogged = userLogged;
    }

    public OrderDTO getTemporalOrder() {
        return temporalOrder;
    }

    public void setTemporalOrder(OrderDTO temporalOrder) {
        this.temporalOrder = temporalOrder;
    }
}
