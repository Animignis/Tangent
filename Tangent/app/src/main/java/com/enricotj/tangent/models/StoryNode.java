package com.enricotj.tangent.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by enricotj on 1/24/2016.
 */
public class StoryNode implements Parcelable {
    private String author;
    private String body;
    private Map<String, Boolean> branches = new HashMap<>();
    private String parent;
    private long timestamp;
    private String title;

    @JsonIgnore
    private String key;

    public StoryNode() {
    }

    public StoryNode(String author, String title, String body, long timestamp, String parent) {
        branches = new HashMap<>();
        this.author = author;
        this.body = body;
        this.parent = parent;
        this.timestamp = timestamp;
        this.title = title;
    }

    protected StoryNode(Parcel in) {
        author = in.readString();
        body = in.readString();
        parent = in.readString();
        timestamp = in.readLong();
        title = in.readString();
        key = in.readString();
    }

    public static final Creator<StoryNode> CREATOR = new Creator<StoryNode>() {
        @Override
        public StoryNode createFromParcel(Parcel in) {
            return new StoryNode(in);
        }

        @Override
        public StoryNode[] newArray(int size) {
            return new StoryNode[size];
        }
    };

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

    public Map<String, Boolean> getBranches() {
        return branches;
    }

    public void setBranches(Map<String, Boolean> branches) {
        this.branches = branches;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(title);
        dest.writeMap(branches);
        dest.writeString(body);
        dest.writeString(parent);
        dest.writeLong(timestamp);
    }
}
