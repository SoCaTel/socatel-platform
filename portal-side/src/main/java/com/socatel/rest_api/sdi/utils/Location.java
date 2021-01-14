package com.socatel.rest_api.sdi.utils;

public class Location {
    private String name;
    private String alternateName;
    private String countryCode;


    // Getter Methods

    public String getName() {
        return name;
    }

    public String getAlternateName() {
        return alternateName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    // Setter Methods

    public void setName(String name) {
        this.name = name;
    }

    public void setAlternateName(String alternateName) {
        this.alternateName = alternateName;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}