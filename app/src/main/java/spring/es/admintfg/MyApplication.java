package spring.es.admintfg;

import android.app.Application;

import spring.es.admintfg.dto.UserDTO;

public class MyApplication extends Application {

    private String token;
    private Boolean isAdmin = false;
    private UserDTO userLogged;

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
}
