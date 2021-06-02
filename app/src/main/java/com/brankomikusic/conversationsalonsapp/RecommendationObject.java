package com.brankomikusic.conversationsalonsapp;

import com.google.firebase.Timestamp;

/**
 * Class for creating objects that hold data from a specific recommendation.
 *
 * @author Branko Mikusic
 */
public class RecommendationObject {
    private String authorUID;
    private Timestamp creationTime;
    private String title;

    public RecommendationObject(){

    }

    public String getAuthorUID() {
        return authorUID;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public void setTitle(String text) {
        this.title = text;
    }
}
