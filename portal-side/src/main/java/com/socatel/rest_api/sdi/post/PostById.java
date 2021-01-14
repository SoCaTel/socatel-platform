package com.socatel.rest_api.sdi.post;

import com.socatel.rest_api.sdi.utils.Creator;
import com.socatel.rest_api.sdi.utils.Location;
import com.socatel.rest_api.sdi.utils.Owner;

import java.util.LinkedList;
import java.util.List;

public class PostById {
    private String description;
    private String creationDate;
    private String language;
    private int numLikes;
    private int numReplies;
    private List<String> topics;
    private Location location;
    private Owner owner;
    private Creator creator;

    public PostById() {
        topics = new LinkedList<>();
    }

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

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
                ", topics=" + topics +
                ", location=" + location +
                ", owner=" + owner +
                ", creator=" + creator +
                '}';
    }
}
