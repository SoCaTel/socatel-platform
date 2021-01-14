package com.socatel.models;

import com.socatel.utils.enums.ServiceStatus;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "so_service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int id;

    @Column(name = "service_name")
    private String name;

    @Column(name = "service_description")
    private String description;

    @Column(name = "native_service_description")
    private String nativeDescription;

    @Column(name = "service_status")
    @Enumerated
    private ServiceStatus status;

    @Column(name = "service_website")
    private String website;

    @Column(name = "service_hashtag")
    private String hashtag;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne
    @JoinColumn(name = "locality_id")
    private Locality locality;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document logo;

    @ManyToMany
    @JoinTable(
            name = "so_service_theme",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id"))
    private Set<Theme> themes;

    @Column(name = "twitter_screen_name")
    private String twitterScreenName;

    @Column(name = "twitter_account_description")
    private String twitterAccountDescription;

    @Column(name = "twitter_user_id")
    private Integer twitterUserId;

    @Column(name = "twitter_oauth_token")
    private String twitterOauthToken;

    @Column(name = "twitter_oauth_secret")
    private String twitterOauthSecret;

    @Column(name = "facebook_oauth_token")
    private String facebookOauthToken;

    @Column(name = "facebook_page_id")
    private String facebookPageId;

    @Transient
    private List<Document> documents;

    public String getDescription(String langCode) {
        if (nativeDescription == null || nativeDescription.isEmpty() || nativeDescription.trim().equals("")) return description;
        if (description == null || description.isEmpty() || description.trim().equals("")) return nativeDescription;
        if (langCode.equalsIgnoreCase(language.getCode()))
            return nativeDescription;
        else return description;
    }

    public Service() {
        status = ServiceStatus.SUGGESTED;
    }

    public boolean isApproved() {
        return status.equals(ServiceStatus.APPROVED);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public Set<Theme> getThemes() {
        return themes;
    }

    public void setThemes(Set<Theme> themes) {
        this.themes = themes;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
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

    public void setTwitterAccountDescription(String twitterAccountDescription) {
        this.twitterAccountDescription = twitterAccountDescription;
    }

    public Integer getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(Integer twitterUserId) {
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

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getNativeDescription() {
        return nativeDescription;
    }

    public void setNativeDescription(String nativeDescription) {
        this.nativeDescription = nativeDescription;
    }

    public Document getLogo() {
        return logo;
    }

    public void setLogo(Document logo) {
        this.logo = logo;
    }
}
