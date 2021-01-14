package com.socatel.models;

import com.socatel.components.Methods;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.utils.enums.GroupStatusEnum;
import com.socatel.utils.enums.OnOffEnum;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "so_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private int id;

    @Column(name = "group_name")
    @Size(min = 2, max = 80, message = "{group_name.length}")
    private String name;

    @Size(min = 2, max = 1000, message = "{group_description.length}")
    @Column(name = "group_description")
    private String description;

    @Size(min = 2)
    @Column(name = "group_step1_summary")
    private String step1summary;

    @Size(min = 2)
    @Column(name = "group_step2_summary")
    private String step2summary;

    @Size(min = 2)
    @Column(name = "group_step3_summary")
    private String step3summary;

    @Size(min = 2)
    @Column(name = "group_step4_summary")
    private String step4summary;

    @Column(name = "group_status")
    @Enumerated(EnumType.ORDINAL)
    private GroupStatusEnum status;

    @Column(name = "group_create_time")
    private Timestamp timestamp;

    @Column(name = "group_next_step_time")
    private Timestamp nextStepTimestamp;

    @ManyToOne
    @JoinColumn(name="language_id", nullable = false)
    private Language language;

    @ManyToOne
    @JoinColumn(name="user_initiator_id", nullable = false)
    private User initiator;

    @ManyToOne
    @JoinColumn(name="user_facilitator_id")
    private User facilitator;

    @ManyToOne
    @JoinColumn(name="locality_id", nullable = false)
    private Locality locality;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<GroupUserRelation> usersRelation;

    @ManyToMany
    @JoinTable(
            name = "so_group_theme",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id"))
    private Set<Theme> themes;

    @Transient
    private boolean hasIdea;

    @Transient
    private boolean hasOrganisation;

    @Transient
    private boolean hasProposals;

    @Column(name = "group_meeting_display")
    @Enumerated(EnumType.ORDINAL)
    private OnOffEnum meetingDisplay;

    @Column(name = "group_meeting_code")
    private String meetingCode;

    public String fancyTimestamp() {
        return Methods.formatDate(new Date(timestamp.getTime()));
    }

    public String fancyNextStepTimestamp() {
        return Methods.formatDate(new Date(nextStepTimestamp.getTime()));
    }

    public Long milisRemaining() {
        return Methods.milisRemaining(nextStepTimestamp);
    }

    public String getStepNumber() {
        return String.valueOf(status.ordinal());
    }

    public boolean isInProcessOrCompleted() {
        return GroupStatusEnum.IDEATION.ordinal() <= status.ordinal() && status.ordinal() <= GroupStatusEnum.COMPLETED.ordinal();
    }

    private String generateJitsiLink()
    {
        // Random string length
        int n = 32;

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            // generate a random number between 0 to AlphaNumericString variable length
            int index = (int)(AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }

        return "https://meet.jit.si/" + sb.toString();
    }

    public Group() {
        this.meetingCode = generateJitsiLink();
        this.meetingDisplay = OnOffEnum.OFF;
    }

    public Group(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.language = group.getLanguage();
        this.description = group.getDescription();
        this.initiator = group.getInitiator();
        this.locality = group.getLocality();
        this.meetingCode = generateJitsiLink();
        this.meetingDisplay = OnOffEnum.OFF;
    }

    public boolean hasIdea() {
        return hasIdea;
    }

    public void setHasIdea(boolean hasIdea) {
        this.hasIdea = hasIdea;
    }

    public boolean hasOrganisation() {
        return hasOrganisation;
    }

    public void setHasOrganisation(boolean hasOrganisation) {
        this.hasOrganisation = hasOrganisation;
    }

    public boolean hasProposals() {
        return hasProposals;
    }

    public void setHasProposals(boolean hasProposals) {
        this.hasProposals = hasProposals;
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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Set<GroupUserRelation> getUsersRelation() {
        return usersRelation;
    }

    public void setUsersRelation(Set<GroupUserRelation> usersRelation) {
        this.usersRelation = usersRelation;
    }

    public GroupStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GroupStatusEnum status) {
        this.status = status;
    }

    public Set<Theme> getThemes() {
        return themes;
    }

    public void setThemes(Set<Theme> themes) {
        this.themes = themes;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Timestamp getNextStepTimestamp() {
        return nextStepTimestamp;
    }

    public void setNextStepTimestamp(Timestamp nextStepTimestamp) {
        this.nextStepTimestamp = nextStepTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getStep1summary() {
        return step1summary;
    }

    public void setStep1summary(String step1summary) {
        this.step1summary = step1summary;
    }

    public String getStep2summary() {
        return step2summary;
    }

    public void setStep2summary(String step2summary) {
        this.step2summary = step2summary;
    }

    public String getStep3summary() {
        return step3summary;
    }

    public void setStep3summary(String step3summary) {
        this.step3summary = step3summary;
    }

    public String getStep4summary() {
        return step4summary;
    }

    public void setStep4summary(String step4summary) {
        this.step4summary = step4summary;
    }

    public User getFacilitator() {
        return facilitator;
    }

    public void setFacilitator(User facilitator) {
        this.facilitator = facilitator;
    }

    public boolean isHasIdea() {
        return hasIdea;
    }

    public boolean isHasOrganisation() {
        return hasOrganisation;
    }

    public boolean isHasProposals() {
        return hasProposals;
    }

    public OnOffEnum getMeetingDisplay() {
        return meetingDisplay;
    }

    public boolean isMeetingDisplayed() {
        return meetingDisplay.equals(OnOffEnum.ON);
    }

    public void setMeetingDisplay(OnOffEnum meetingDisplay) {
        this.meetingDisplay = meetingDisplay;
    }

    public String getMeetingCode() {
        if (this.meetingCode == null) {
            this.meetingCode = generateJitsiLink();
        }
        return this.meetingCode;
    }

    public void setMeetingCode(String meetingCode) {
        this.meetingCode = meetingCode;
    }
}
