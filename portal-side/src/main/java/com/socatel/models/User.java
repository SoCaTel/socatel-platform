package com.socatel.models;

import com.socatel.components.Methods;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.models.relationships.PropositionUserVote;
import com.socatel.models.relationships.UserFeedbackVote;
import com.socatel.models.relationships.UserPostVote;
import com.socatel.utils.enums.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "so_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "user_username")
    @NotEmpty(message = "{user_username.notEmpty}")
    private String username;

    @Column(name = "user_password")
    @Pattern(regexp = "(.{8,64})", message = "{user_password.length}")
    private String password;

    @Transient
    private String matchingPassword;

    @Column(name = "user_email")
    @NotEmpty(message = "{user_email.notEmpty}")
    @Pattern(regexp =
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\" +
            "x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*" +
            "[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:" +
            "25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a" +
            "\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
            message = "{user_email.format}")
    private String email;

    @Column(name = "user_enabled")
    @Enumerated(EnumType.ORDINAL)
    private EnableEnum enabled;

    @Column(name = "user_anonymized")
    @Enumerated(EnumType.ORDINAL)
    private AnonEnum anonymized;

    @Column(name = "user_role")
    @Enumerated(EnumType.ORDINAL)
    private UserRoleEnum role;

    @Column(name = "user_create_time")
    private Timestamp createTime;

    @Column(name = "user_profile")
    @Enumerated(EnumType.ORDINAL)
    private ProfileEnum profile;

    @Column(name = "user_gender")
    @Enumerated(EnumType.ORDINAL)
    private GenderEnum gender;

    @Column(name = "user_description")
    private String description;

    @Column(name = "user_age_range")
    @Enumerated(EnumType.ORDINAL)
    private AgeEnum ageRange;

    @ManyToOne
    @JoinColumn(name = "language_id_first", nullable = false)
    private Language firstLang;

    @ManyToOne
    @JoinColumn(name = "language_id_second")
    private Language secondLang;

    @ManyToOne
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id_parent")
    private Locality parentLocality;

    @Column(name = "user_org_role")
    @Enumerated(EnumType.ORDINAL)
    private OrgRoleEnum orgRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<GroupUserRelation> topicsRelation;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserPostVote> postsLiked;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<PropositionUserVote> propositionsLiked;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserFeedbackVote> feedbacksLiked;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "so_user_theme",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id"))
    private Set<Theme> themes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "so_user_skill",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "so_user_answer",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "answer_id"))
    private Set<Answer> votedAnswers;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document picture;

    /**
     * Notification options
     */

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_notif_email")
    private OnOffEnum notifyByEmail; // 0: off, 1: on

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_notif_new_topic")
    private OnOffEnum notifyMessageByEmail; // 0: off, 1: on

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_notif_all")
    private OnOffEnum notifyAll; // 0: off, 1: on

    /**
     * Data usage
     * @Enumerated(EnumType.ORDINAL)
     * @Column(name = "user_personal_data_usage")
     * private OnOffEnum usePersonalData; // 0: off, 1: on
     */

    public User() {
        enabled = EnableEnum.DISABLED;
        role = UserRoleEnum.USER;
        anonymized = AnonEnum.NOT_ANONYMIZED;
        notifyByEmail = OnOffEnum.OFF;
        notifyMessageByEmail = OnOffEnum.OFF;
        notifyAll = OnOffEnum.OFF;
    }

    public void addAnswer(Answer answer) {
        votedAnswers.add(answer);
    }

    public void removeAnswer(Answer answer) {
        votedAnswers.remove(answer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    public String fancyTimestamp() {
        return Methods.formatDate(new Date(createTime.getTime()));
    }

    /**
     * Check if the passwords match
     * @return passwordsMatch?
     */
    public boolean passwordsMatch() {
        return password.equals(matchingPassword);
    }

    /**
     * Check if user data is anonymous
     * @return anonymized?
     */
    public boolean isAnonymized() {
        return anonymized.equals(AnonEnum.ANONYMIZED);
    }

    public boolean isOrgCreator() {
        return orgRole.equals(OrgRoleEnum.ADMIN);
    }

    /**
     * Anonymize user data
     */
    public void logicalDelete(Locality locality) {
        username = UUID.randomUUID().toString();
        email = UUID.randomUUID().toString() + "@" + email.split("@")[1];
        password = UUID.randomUUID().toString();
        createTime = null;
        role = UserRoleEnum.USER;
        this.locality = locality;
        parentLocality = locality;
        gender = null;
        description = null;
        anonymized = AnonEnum.ANONYMIZED;
        enabled = EnableEnum.DISABLED;
        orgRole = null;
        organisation = null;
        profile = null;
        secondLang = null;
        ageRange = null;
    }

    /**
     * Calculate the percentage of the profile completion
     * @return %
     */
    public int calculateProfileCompletion() {
        int count, total;
        count = total = 6;
        total++; if (profile != null) count++;
        total++; if (description != null && !description.isEmpty()) count++;
        total++; if (gender != null) count++;
        total++; if (secondLang != null) count++;
        total++; if (skills != null && !skills.isEmpty()) count++;
        total++; if (themes != null && !themes.isEmpty()) count++;
        return (count * 100) / total;
    }

    public boolean isModerator() {
        return role.equals(UserRoleEnum.MODERATOR);
    }

    public boolean isFacilitator() {
        return role.equals(UserRoleEnum.FACILITATOR);
    }

    public int countTopicsCreated() {
        int count = 0;
        for (GroupUserRelation groupUserRelation : topicsRelation) {
            if(groupUserRelation.getRelation().equals(GroupUserRelationEnum.CREATED))
                count++;
        }
        return count;
    }

    public int countTopicsContributed() {
        int count = 0;
        for (GroupUserRelation groupUserRelation : topicsRelation) {
            if(groupUserRelation.getRelation().equals(GroupUserRelationEnum.CONTRIBUTOR))
                count++;
        }
        return count;
    }

    public int countTopicsSubscribed() {
        int count = 0;
        for (GroupUserRelation groupUserRelation : topicsRelation) {
            if(groupUserRelation.getRelation().equals(GroupUserRelationEnum.SUBSCRIBED))
                count++;
        }
        return count;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        LinkedList<GrantedAuthority> roles = new LinkedList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (role == UserRoleEnum.MODERATOR)
            roles.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
        else if (role == UserRoleEnum.FACILITATOR)
            roles.add(new SimpleGrantedAuthority("ROLE_FACILITATOR"));
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled != EnableEnum.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled == EnableEnum.ENABLED;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public EnableEnum getEnabled() {
        return enabled;
    }

    public void setEnabled(EnableEnum enabled) {
        this.enabled = enabled;
    }

    public AnonEnum getAnonymized() {
        return anonymized;
    }

    public void setAnonymized(AnonEnum anonymized) {
        this.anonymized = anonymized;
    }

    public Language getFirstLang() {
        return firstLang;
    }

    public void setFirstLang(Language firstLang) {
        this.firstLang = firstLang;
    }

    public Language getSecondLang() {
        return secondLang;
    }

    public void setSecondLang(Language secondLang) {
        this.secondLang = secondLang;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
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

    public OrgRoleEnum getOrgRole() {
        return orgRole;
    }

    public void setOrgRole(OrgRoleEnum orgRole) {
        this.orgRole = orgRole;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Locality getParentLocality() {
        return parentLocality;
    }

    public void setParentLocality(Locality parentLocality) {
        this.parentLocality = parentLocality;
    }

    public Set<GroupUserRelation> getTopicsRelation() {
        return topicsRelation;
    }

    public void setTopicsRelation(Set<GroupUserRelation> topicsRelation) {
        this.topicsRelation = topicsRelation;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public AgeEnum getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeEnum ageRange) {
        this.ageRange = ageRange;
    }

    public Set<Theme> getThemes() {
        return themes;
    }

    public void setThemes(Set<Theme> themes) {
        this.themes = themes;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }

    public OnOffEnum getNotifyByEmail() {
        return notifyByEmail;
    }

    public void setNotifyByEmail(OnOffEnum notifyByEmail) {
        this.notifyByEmail = notifyByEmail;
    }

    public OnOffEnum getNotifyMessageByEmail() {
        return notifyMessageByEmail;
    }

    public void setNotifyMessageByEmail(OnOffEnum notifyMessageByEmail) {
        this.notifyMessageByEmail = notifyMessageByEmail;
    }

    public OnOffEnum getNotifyAll() {
        return notifyAll;
    }

    public void setNotifyAll(OnOffEnum notifyAll) {
        this.notifyAll = notifyAll;
    }

    public Document getPicture() {
        return picture;
    }

    public void setPicture(Document picture) {
        this.picture = picture;
    }

    public Set<UserPostVote> getPostsLiked() {
        return postsLiked;
    }

    public void setPostsLiked(Set<UserPostVote> postsLiked) {
        this.postsLiked = postsLiked;
    }

    public Set<PropositionUserVote> getPropositionsLiked() {
        return propositionsLiked;
    }

    public void setPropositionsLiked(Set<PropositionUserVote> propositionsLiked) {
        this.propositionsLiked = propositionsLiked;
    }

    public Set<UserFeedbackVote> getFeedbacksLiked() {
        return feedbacksLiked;
    }

    public void setFeedbacksLiked(Set<UserFeedbackVote> feedbacksLiked) {
        this.feedbacksLiked = feedbacksLiked;
    }

    public Set<Answer> getVotedAnswers() {
        return votedAnswers;
    }

    public void setVotedAnswers(Set<Answer> votedAnswers) {
        this.votedAnswers = votedAnswers;
    }
}
