package com.enricotj.tangent;

import java.util.Date;
import java.util.List;

/**
 * Created by enricotj on 1/13/2016.
 */
public class StoryNode {
    private Story story;
    private StoryNode parent;

    private int id;
    private String title;
    private String author;
    private String body;
    private Date timestamp;

    private List<StoryNode> branches;

    public StoryNode() {

    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public StoryNode getParent() {
        return parent;
    }

    public Story getStory() {
        return story;
    }

    public List<StoryNode> getBranches() {
        return branches;
    }
}
