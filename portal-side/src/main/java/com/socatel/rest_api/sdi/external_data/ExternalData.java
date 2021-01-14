package com.socatel.rest_api.sdi.external_data;

public class ExternalData {
    private String identifier;
    private String webLink;

    public ExternalData() {}

    public ExternalData(String identifier, String webLink) {
        this.identifier = identifier;
        this.webLink = webLink;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    public String toString() {
        return "{ Identifier: " + identifier + "\n" +
                "WebLink:" + webLink + " }";
    }
}
