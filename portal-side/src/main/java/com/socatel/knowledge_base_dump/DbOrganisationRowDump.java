package com.socatel.knowledge_base_dump;

import com.socatel.models.Organisation;
import com.socatel.utils.enums.OrganisationStructureEnum;

public class DbOrganisationRowDump {
    private Integer organisation_id;
    private String organisation_name;
    private Integer organisation_structure;
    private String organisation_website;
    private String twitter_screen_name;
    private String twitter_account_description;
    private Long twitter_user_id;
    private String twitter_oauth_token;
    private String twitter_oauth_secret;
    private String facebook_page_id;
    private String facebook_oauth_token;

    public DbOrganisationRowDump() {

    }

    public DbOrganisationRowDump(Integer organisationId, String name, OrganisationStructureEnum structure, String website, String twitterScreenName, String twitterAccountDescription, Long twitterUserId, String twitterOauthToken, String twitterOauthSecret, String facebookPageId, String facebookOauthToken) {
        this.organisation_id = organisationId;
        this.organisation_name = name;
        this.organisation_structure = structure.ordinal();
        this.organisation_website = website;
        this.twitter_screen_name = twitterScreenName;
        this.twitter_account_description = twitterAccountDescription;
        this.twitter_user_id = twitterUserId;
        this.twitter_oauth_token = twitterOauthToken;
        this.twitter_oauth_secret = twitterOauthSecret;
        this.facebook_page_id = facebookPageId;
        this.facebook_oauth_token = facebookOauthToken;
    }

    public Organisation toModel() {
        Organisation organisation = new Organisation();
        organisation.setId(organisation_id);
        organisation.setName(organisation_name);
        organisation.setStructure(organisation_structure==null?null:OrganisationStructureEnum.values()[organisation_structure]);
        organisation.setWebsite(organisation_website);
        organisation.setTwitterScreenName(twitter_screen_name);
        organisation.setTwitterAccountDescription(twitter_account_description);
        organisation.setTwitterUserId(twitter_user_id);
        organisation.setTwitterOauthToken(twitter_oauth_token);
        organisation.setTwitterOauthSecret(twitter_oauth_secret);
        organisation.setFacebookPageId(facebook_page_id);
        organisation.setFacebookOauthToken(facebook_oauth_token);
        return organisation;
    }

    public Integer getOrganisation_id() {
        return organisation_id;
    }

    public void setOrganisation_id(Integer organisation_id) {
        this.organisation_id = organisation_id;
    }

    public String getOrganisation_name() {
        return organisation_name;
    }

    public void setOrganisation_name(String organisation_name) {
        this.organisation_name = organisation_name;
    }

    public Integer getOrganisation_structure() {
        return organisation_structure;
    }

    public void setOrganisation_structure(Integer organisation_structure) {
        this.organisation_structure = organisation_structure;
    }

    public String getOrganisation_website() {
        return organisation_website;
    }

    public void setOrganisation_website(String organisation_website) {
        this.organisation_website = organisation_website;
    }

    public String getTwitter_screen_name() {
        return twitter_screen_name;
    }

    public void setTwitter_screen_name(String twitter_screen_name) {
        this.twitter_screen_name = twitter_screen_name;
    }

    public String getTwitter_account_description() {
        return twitter_account_description;
    }

    public void setTwitter_account_description(String twitter_account_description) {
        this.twitter_account_description = twitter_account_description;
    }

    public Long getTwitter_user_id() {
        return twitter_user_id;
    }

    public void setTwitter_user_id(Long twitter_user_id) {
        this.twitter_user_id = twitter_user_id;
    }

    public String getTwitter_oauth_token() {
        return twitter_oauth_token;
    }

    public void setTwitter_oauth_token(String twitter_oauth_token) {
        this.twitter_oauth_token = twitter_oauth_token;
    }

    public String getTwitter_oauth_secret() {
        return twitter_oauth_secret;
    }

    public void setTwitter_oauth_secret(String twitter_oauth_secret) {
        this.twitter_oauth_secret = twitter_oauth_secret;
    }

    public String getFacebook_page_id() {
        return facebook_page_id;
    }

    public void setFacebook_page_id(String facebook_page_id) {
        this.facebook_page_id = facebook_page_id;
    }

    public String getFacebook_oauth_token() {
        return facebook_oauth_token;
    }

    public void setFacebook_oauth_token(String facebook_oauth_token) {
        this.facebook_oauth_token = facebook_oauth_token;
    }
}
