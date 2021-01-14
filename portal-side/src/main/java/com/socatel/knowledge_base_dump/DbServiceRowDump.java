package com.socatel.knowledge_base_dump;

import com.socatel.dtos.knowledge_base.ThemeIdDTO;
import com.socatel.models.Language;
import com.socatel.models.Locality;
import com.socatel.models.Service;
import com.socatel.models.Theme;
import com.socatel.repositories.*;
import com.socatel.utils.enums.ServiceStatus;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbServiceRowDump implements Serializable {
    private Integer service_id;
    private String service_name;
    private String service_description;
    private String native_service_description;
    private String service_website;
    private DbLocalityRowDump locality;
    private DbLanguageRowDump language;
    private Integer organisation_id;
    private Integer group_id;
    private Integer service_status;
    private String service_hashtag;
    private List<ThemeIdDTO> themes;
    private String twitter_screen_name;
    private String twitter_account_description;
    private Integer twitter_user_id;
    private String twitter_oauth_token;
    private String twitter_oauth_secret;
    private String facebook_page_id;
    private String facebook_oauth_token;

    public DbServiceRowDump() {
        super();
    }

    public DbServiceRowDump(Integer serviceId, String name, String description, String nativeDescription, String website, Locality locality, Language languageId, Integer organisation_id, Integer group_id, ServiceStatus status, String hashtag, List<ThemeIdDTO> themes, String twitterScreenName, String twitterAccountDescription, Integer twitterUserId, String twitterOauthToken, String twitterOauthSecret, String facebookPageId, String facebookOauthToken) {
        this.service_id = serviceId;
        this.service_name = name;
        this.service_description = description;
        this.native_service_description = nativeDescription;
        this.service_website = website;
        this.locality = new DbLocalityRowDump(locality);
        this.language = new DbLanguageRowDump(languageId);
        this.organisation_id = organisation_id;
        this.group_id = group_id;
        this.service_status = status.ordinal();
        this.service_hashtag = hashtag;
        this.themes = themes;
        this.twitter_screen_name = twitterScreenName;
        this.twitter_account_description = twitterAccountDescription;
        this.twitter_user_id = twitterUserId;
        this.twitter_oauth_token = twitterOauthToken;
        this.twitter_oauth_secret = twitterOauthSecret;
        this.facebook_page_id = facebookPageId;
        this.facebook_oauth_token = facebookOauthToken;
    }

    public Service toModel(LocalityRepository localityRepository, LanguageRepository languageRepository,
                           OrganisationRepository organisationRepository, GroupRepository groupRepository, ThemeRepository themeRepository) {
        Service service = new Service();
        service.setId(service_id);
        service.setName(service_name);
        service.setDescription(service_description);
        service.setNativeDescription(native_service_description);
        service.setWebsite(service_website);
        service.setLocality(locality==null?null:localityRepository.findById(locality.getLocality_id()).orElse(null));
        service.setLanguage(language==null?null:languageRepository.findById(language.getLanguage_id()).orElse(null));
        service.setOrganisation(organisation_id==null?null:organisationRepository.findById(organisation_id).orElse(null));
        service.setGroup(group_id==null?null:groupRepository.findById(group_id).orElse(null));
        service.setStatus(service_status==null?null:ServiceStatus.values()[service_status]);
        service.setHashtag(service_hashtag);
        service.setThemes(toThemes(themes, themeRepository));
        service.setTwitterScreenName(twitter_screen_name);
        service.setTwitterAccountDescription(twitter_account_description);
        service.setTwitterUserId(twitter_user_id);
        service.setTwitterOauthToken(twitter_oauth_token);
        service.setTwitterOauthSecret(twitter_oauth_secret);
        service.setFacebookPageId(facebook_page_id);
        service.setFacebookOauthToken(facebook_oauth_token);
        return service;
    }

    private Set<Theme> toThemes(List<ThemeIdDTO> themes, ThemeRepository themeRepository) {
        Set<Theme> themeSet = new HashSet<>();
        if (themes == null) return themeSet;
        for (ThemeIdDTO t : themes) {
            themeSet.add(themeRepository.findById(t.getTheme_id()).orElse(null));
        }
        return themeSet;
    }

    public Integer getService_id() {
        return service_id;
    }

    public void setService_id(Integer service_id) {
        this.service_id = service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public String getService_website() {
        return service_website;
    }

    public void setService_website(String service_website) {
        this.service_website = service_website;
    }

    public DbLocalityRowDump getLocality() {
        return locality;
    }

    public void setLocality(DbLocalityRowDump locality) {
        this.locality = locality;
    }

    public DbLanguageRowDump getLanguage() {
        return language;
    }

    public void setLanguage(DbLanguageRowDump language) {
        this.language = language;
    }

    public Integer getService_status() {
        return service_status;
    }

    public void setService_status(Integer service_status) {
        this.service_status = service_status;
    }

    public String getService_hashtag() {
        return service_hashtag;
    }

    public void setService_hashtag(String service_hashtag) {
        this.service_hashtag = service_hashtag;
    }

    public List<ThemeIdDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<ThemeIdDTO> themes) {
        this.themes = themes;
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

    public Integer getTwitter_user_id() {
        return twitter_user_id;
    }

    public void setTwitter_user_id(Integer twitter_user_id) {
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

    public Integer getOrganisation_id() {
        return organisation_id;
    }

    public void setOrganisation_id(Integer organisation_id) {
        this.organisation_id = organisation_id;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getNative_service_description() {
        return native_service_description;
    }

    public void setNative_service_description(String native_service_description) {
        this.native_service_description = native_service_description;
    }
}
