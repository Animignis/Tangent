package com.enricotj.tangent;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * Created by enricotj on 1/24/2016.
 */
public class StoryNode {
    private String author;
    private String body;
    private Map<String, Boolean>[] branches;
    private String parent;
    private String timestamp;
    private String title;

    @JsonIgnore
    private String key;

    public StoryNode() {

    }

    public StoryNode(String author, String body, Map<String, Boolean>[] branches, String parent, String timestamp, String title) {
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

    public Map<String, Boolean>[] getBranches() {
        return branches;
    }

    public void setBranches(Map<String, Boolean>[] branches) {
        this.branches = branches;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
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
