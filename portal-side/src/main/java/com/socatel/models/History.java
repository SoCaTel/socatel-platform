package com.socatel.models;

import com.socatel.components.Methods;
import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.VisibleEnum;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "so_history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private int id;

    @Column(name = "history_text")
    private String text;

    @Column(name = "history_time")
    private Timestamp timestamp;

    @Column(name = "history_type")
    private HistoryTypeEnum type;

    @Column(name = "history_level")
    private VisibleEnum level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisation_id")
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_id")
    private Group group;

    public History() {
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public History(User user, Organisation organisation, Group group, String text, HistoryTypeEnum type, VisibleEnum level) {
        this.text = text;
        this.user = user;
        this.organisation = organisation;
        this.group = group;
        this.type = type;
        this.level = level;
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String fancyTimestamp() {
        return Methods.formatDate(new Date(timestamp.getTime()));
    }

    public String getCorrespondingName() {
        if (organisation != null) return organisation.getName();
        if (group != null) return group.getName();
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public HistoryTypeEnum getType() {
        return type;
    }

    public void setType(HistoryTypeEnum type) {
        this.type = type;
    }

    public VisibleEnum getLevel() {
        return level;
    }

    public void setLevel(VisibleEnum level) {
        this.level = level;
    }
}
