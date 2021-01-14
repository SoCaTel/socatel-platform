package com.socatel.rest_api.sdi.utils;

public class Owner {
    private String identifier = null;
    private String title = null;
    private String description = null;
    private String webLink = null;
    private String imageLink = null;
    private String language = null;
    private String numLikes = null;


    // Getter Methods

    public String getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getWebLink() {
        return webLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getLanguage() {
        return language;
    }

    public String getNumLikes() {
        return numLikes;
    }

    // Setter Methods

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setNumLikes(String numLikes) {
        this.numLikes = numLikes;
    }
}