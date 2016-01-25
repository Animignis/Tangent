package com.enricotj.tangent;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by enricotj on 1/24/2016.
 */
public class StoryNode {
    private String author;
    private String body;
    private int[] branches;
    private int parent;
    private String timestamp;
    private String title;

    @JsonIgnore
    private String key;

    public StoryNode() {

    }

    public StoryNode(String author, String body, int[] branches, int parent, String timestamp, String title) {
        this.author = author;
        this.body = body;
        this.branches = branches;
        this.parent = parent;
        this.timestamp = timestamp;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int[] getBranches() {
        return branches;
    }

    public void setBranches(int[] branches) {
        this.branches = branches;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
