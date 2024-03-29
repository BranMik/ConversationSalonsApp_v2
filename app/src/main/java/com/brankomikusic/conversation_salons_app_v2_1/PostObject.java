package com.brankomikusic.conversation_salons_app_v2_1;

import com.google.firebase.Timestamp;

/**
 * Class for creating objects that hold data from the post to the conversation.
 *
 * @author Branko Mikusic
 */
public class PostObject {
    private String postId;
    private String authorUID;
    private Timestamp creationTime;
    private String text;

    public PostObject(){

    }

    public String getAuthorUID() {
        return authorUID;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public String getText() {
        return text;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public void setText(String text) {
        this.text = text;
    }
}
