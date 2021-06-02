package com.brankomikusic.conversationsalonsapp;

import com.google.firebase.Timestamp;

/**
 * Class for creating objects that hold data about posted invitations
 *
 * @author Branko Mikusic
 */
class InvitationObject {

    private String authorUID;
    private String title;
    private Timestamp creationTime;

    public InvitationObject(){

    }

    public String getAuthorUID() {
        return authorUID;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public void setTitle(String text) {
        this.title = text;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }
}
