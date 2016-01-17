package com.enricotj.tangent;

import java.util.Date;
import java.util.List;

/**
 * Created by enricotj on 1/13/2016.
 */
public class Story {
    private StoryNode root;

    private List<String> tags;

    private int views;
    private double viewsPerNode;

    private Date lastUpdated;

    public Story() {

    }

    public StoryNode getRoot() {
        return root;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getViews() {
        return views;
    }

    public double getViewsPerNode() {
        return viewsPerNode;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }
}
