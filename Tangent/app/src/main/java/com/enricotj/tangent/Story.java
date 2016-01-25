package com.enricotj.tangent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

/**
 * Created by enricotj on 1/13/2016.
 */
public class Story {
    private String lastupdated;
    private int numfavorites;
    private int root;
    private int size;
    private int views;

    @JsonIgnore
    private String key;

    public Story() {

    }

    public Story(String lastupdated, int numfavorites, int root, int size, int views) {
        this.lastupdated = lastupdated;
        this.numfavorites = numfavorites;
        this.root = root;
        this.size = size;
        this.views = views;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public int getNumfavorites() {
        return numfavorites;
    }

    public void setNumfavorites(int numfavorites) {
        this.numfavorites = numfavorites;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
