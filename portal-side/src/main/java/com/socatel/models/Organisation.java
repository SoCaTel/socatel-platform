package com.socatel.models;

import com.socatel.utils.enums.OrganisationStructureEnum;

import javax.persistence.*;

@Entity
@Table(name = "so_organisation")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organisation_id")
    private int id;

    @Column(name = "organisation_name")
    private String name;

    @Column(name = "organisation_structure")
    @Enumerated(EnumType.ORDINAL)
    private OrganisationStructureEnum structure;

    @Column(name = "organisation_website")
    private String website;

    @Column(name = "twitter_screen_name")
    private String twitterScreenName;

    @Column(name = "twitter_account_description")
    private String twitterAccountDescription;

    @Column(name = "twitter_user_id")
    private Long twitterUserId;

    @Column(name = "twitter_oauth_token")
    private String twitterOauthToken;

    @Column(name = "twitter_oauth_secret")
    private String twitterOauthSecret;

    @Column(name = "facebook_oauth_token")
    private String facebookOauthToken;

    @Column(name = "facebook_page_id")
    private String facebookPageId;

    public Organisation() {

    }

    public Organisation(String name, OrganisationStructureEnum structure, String website, String twitterScreenName, String twitterAccountDescription, Long twitterUserId) {
        this.name = name;
        this.structure = structure;
        this.website = website;
        this.twitterScreenName = twitterScreenName;
        this.twitterAccountDescription = twitterAccountDescription;
        this.twitterUserId = twitterUserId;
    }

    public boolean isTwitterConnected() {
        return twitterScreenName != null || twitterUserId != null || twitterAccountDescription != null || twitterOauthToken != null || twitterOauthSecret != null;
    }

    public boolean isFacebookConnected() {
        return facebookOauthToken != null || facebookPageId != null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }

    public void setTwitterScreenName(String twitterScreenName) {
        this.twitterScreenName = twitterScreenName;
    }

    public String getTwitterAccountDescription() {
        return twitterAccountDescription;
    }

    public void setTwitterAccountDescription(String accountDescription) {
        this.twitterAccountDescription = accountDescription;
    }

    public Long getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(Long twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public String getTwitterOauthToken() {
        return twitterOauthToken;
    }

    public void setTwitterOauthToken(String twitterOauthToken) {
        this.twitterOauthToken = twitterOauthToken;
    }

    public String getTwitterOauthSecret() {
        return twitterOauthSecret;
    }

    public void setTwitterOauthSecret(String twitterOauthSecret) {
        this.twitterOauthSecret = twitterOauthSecret;
    }

    public OrganisationStructureEnum getStructure() {
        return structure;
    }

    public void setStructure(OrganisationStructureEnum structure) {
        this.structure = structure;
    }

    public String getFacebookOauthToken() {
        return facebookOauthToken;
    }

    public void setFacebookOauthToken(String facebookOauthToken) {
        this.facebookOauthToken = facebookOauthToken;
    }

    public String getFacebookPageId() {
        return facebookPageId;
    }

    public void setFacebookPageId(String facebookPageId) {
        this.facebookPageId = facebookPageId;
    }
}
