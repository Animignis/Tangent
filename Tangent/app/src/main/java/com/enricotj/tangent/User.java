package com.enricotj.tangent;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by enricotj on 1/13/2016.
 */
public class User {
    private String username;

    @JsonIgnore
    private String key;

    public User() {

    }

    public User(String username) {
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
