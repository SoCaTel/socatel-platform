package com.socatel.models;

import com.socatel.components.Methods;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "so_message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int id;

    @Column(name = "message_text")
    @NotEmpty(message = "{message.notEmpty}")
    private String text;

    @Column(name = "message_time")
    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    public Message() {
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Message(Chat chat, User user, String text) {
        this.user = user;
        this.chat = chat;
        this.text = text;
        timestamp = new Timestamp(System.currentTimeMillis());
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

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

}
