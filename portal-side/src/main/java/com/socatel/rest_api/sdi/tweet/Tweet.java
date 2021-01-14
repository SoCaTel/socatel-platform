package com.socatel.rest_api.sdi.tweet;

import com.socatel.rest_api.sdi.utils.Creator;
import com.socatel.rest_api.sdi.utils.Owner;

public class Tweet {
    private String identifier;
    private String description;
    private String creationDate;
    private String language;
    private int numLikes;
    private int numReplies;
    private Owner owner;
    private Creator creator;

    public Tweet() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getNumReplies() {
        return numReplies;
    }

    public void setNumReplies(int numReplies) {
        this.numReplies = numReplies;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "PostById{" +
                "description='" + description + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", language='" + language + '\'' +
                ", numLikes=" + numLikes +
                ", numReplies=" + numReplies +
                ", owner=" + owner +
                ", creator=" + creator +
                '}';
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
