package com.socatel.rest_api.sdi.external_service;

import com.socatel.rest_api.sdi.utils.Creator;
import com.socatel.rest_api.sdi.utils.Location;

public class ServiceByTopic {
    private String identifier;
    private String title;
    private String description;
    private String language;
    private String webLink;
    private Creator creator;
    private Location location;

    public ServiceByTopic() {}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ServiceByTopic{" +
                "identifier='" + identifier + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", webLink='" + webLink + '\'' +
                ", creator=" + creator +
                ", location=" + location +
                '}';
    }
}
