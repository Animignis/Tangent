package com.enricotj.tangent.models;

import java.util.Map;

/**
 * Created by enricotj on 2/11/2016.
 */
public class CurrentUser {

    private static User user = new User( );

    private CurrentUser() {

    }

    public static User getInstance( ) {
        return user;
    }

    public static void setUid(String uid) {
        user.setKey(uid);
    }

    protected String getUid() {
        return user.getKey();
    }

    protected String getUsername() {
        return user.getUsername();
    }

    protected Map<String, Boolean> getFavorites() {
        return user.getFavorites();
    }

    protected void setFavorites(Map<String, Boolean> favorites) {
        user.setFavorites(favorites);
    }

    public static void setInstance(User newUser) {
        user = newUser;
    }

}
