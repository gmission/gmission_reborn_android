package hk.ust.gmission.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{
    public final static String USER_ROLE_REQUESTER = "requester";
    public final static String USER_ROLE_WORKER = "worker";
    public final static String USER_ROLE_ADMINISTRATOR = "admin";

    int id;
    @Expose
    String username;
    @Expose String email;
    @Expose String password;
    int credit;
    boolean active;
    String confirmed_at;
    String token;
    String[] roles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getConfirmed_at() {
        return confirmed_at;
    }

    public void setConfirmed_at(String confirmed_at) {
        this.confirmed_at = confirmed_at;
    }


    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public boolean hasRole(String role){
        if( this.roles == null || this.roles.length == 0 ) return false;
        for(String r : this.roles){
            if(r.equals(role)) return true;
        }
        return false;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}