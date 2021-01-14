package com.socatel.dtos;

import com.socatel.models.User;
import com.socatel.utils.enums.AgeEnum;
import com.socatel.utils.enums.GenderEnum;
import com.socatel.utils.enums.OrgRoleEnum;
import com.socatel.utils.enums.ProfileEnum;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserData implements Serializable {
    private String username;
    private String email;
    private Timestamp createTime;
    private ProfileEnum profile;
    private GenderEnum gender;
    private String description;
    private AgeEnum ageRange;
    private String firstLang;
    private String secondLang;
    private String locality;
    private OrgRoleEnum orgRole;
    private String organisation;
    //private Set<GroupUserRelation> topicsRelation;
    //private Set<UserPostVote> postsLiked;
    //private Set<PropositionUserVote> propositionsLiked;

    public UserData(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createTime = user.getCreateTime();
        this.profile = user.getProfile();
        this.gender = user.getGender();
        this.description = user.getDescription();
        this.ageRange = user.getAgeRange();
        this.firstLang = user.getFirstLang().getName();
        this.secondLang = user.getSecondLang() != null ? user.getSecondLang().getName() : null;
        this.locality = user.getLocality().getName();
        this.orgRole = user.getOrgRole();
        this.organisation = user.getOrganisation() != null ? user.getOrganisation().getName() : null;
        //this.topicsRelation = user.getTopicsRelation();
        //this.postsLiked = user.getPostsLiked();
        //this.propositionsLiked = user.getPropositionsLiked();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public ProfileEnum getProfile() {
        return profile;
    }

    public void setProfile(ProfileEnum profile) {
        this.profile = profile;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AgeEnum getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeEnum ageRange) {
        this.ageRange = ageRange;
    }

    public String getFirstLang() {
        return firstLang;
    }

    public void setFirstLang(String firstLang) {
        this.firstLang = firstLang;
    }

    public String getSecondLang() {
        return secondLang;
    }

    public void setSecondLang(String secondLang) {
        this.secondLang = secondLang;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public OrgRoleEnum getOrgRole() {
        return orgRole;
    }

    public void setOrgRole(OrgRoleEnum orgRole) {
        this.orgRole = orgRole;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

}
