package com.brankomikusic.conversationsalonsapp_v2;

import com.google.firebase.Timestamp;

/**
 * Class for creating objects that hold data about a conversation topic.
 *
 * @author Branko Mikusic
 */
public class ConversationObject {

    private String authorUID;
    private String title;
    private String text;
    private Timestamp creationTime;

    public ConversationObject(){

    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public String getAuthorUID() {
        return authorUID;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }
}
