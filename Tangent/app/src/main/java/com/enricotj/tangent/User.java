package com.enricotj.tangent;

/**
 * Created by enricotj on 1/13/2016.
 */
public class User {

    private static User instance = new User();

    private String username;

    private User() {

    }

    public static User getInstance() {
        return instance;
    }

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
