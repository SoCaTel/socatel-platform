package com.socatel.models;

import com.socatel.components.Methods;
import com.socatel.utils.enums.NewEnum;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "so_notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notif_id")
    private int id;

    @Column(name = "notif_text")
    private String text;

    @Column(name = "notif_time")
    private Timestamp timestamp;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "notif_new")
    private NewEnum newNotif; // 0: yes 1: no

    @Column(name = "notif_url")
    private String url;

    @Column(name = "notif_reference")
    private String reference;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Notification() {
        timestamp = new Timestamp(System.currentTimeMillis());
        newNotif = NewEnum.NEW;
    }

    public Notification(String text, User userTo, String url, String reference) {
        this.text = text;
        this.user = userTo;
        this.url = url;
        this.reference = reference;
        newNotif = NewEnum.NEW;
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public boolean isNewNotification() {
        return newNotif.equals(NewEnum.NEW);
    }

    public String fancyTimestamp() {
        return Methods.formatDate(new Date(timestamp.getTime()));
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public NewEnum getNewNotif() {
        return newNotif;
    }

    public void setNewNotif(NewEnum newNotif) {
        this.newNotif = newNotif;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
