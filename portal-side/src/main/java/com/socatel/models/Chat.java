package com.socatel.models;

import com.socatel.components.Methods;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "so_chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id_1", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id_2", nullable = false)
    private User user2;

    @Column(name = "chat_lastseen_user_1")
    private Timestamp lastSeen1;

    @Column(name = "chat_lastseen_user_2")
    private Timestamp lastSeen2;

    @Column(name = "chat_lastmessage")
    private Timestamp lastMessage;

    public Chat() {}

    public Chat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.lastSeen1 = new Timestamp(System.currentTimeMillis());
        this.lastSeen2 = new Timestamp(System.currentTimeMillis());
    }

    public String fancyTimestamp() {
        return Methods.formatDate(new Date(lastMessage.getTime()));
    }

    /**
     * Get the other user in the chat
     * @param user user
     * @return the other user
     * @implNote this has to be called only if user belongs to chat
     */
    public User getOtherUser(User user) {
        if (user.equals(user1)) return user2;
        else return user1;
    }

    /**
     * Check if user has new messages in this chat
     * @param user user
     * @return true or false
     * @implNote this has to be called only if user belongs to chat
     */
    public boolean hasNewMessages(User user) {
        if (lastMessage == null) return false;
        if (user.equals(user1)) return lastSeen1 == null || lastSeen1.before(lastMessage);
        else return lastSeen2 == null || lastSeen2.before(lastMessage);
    }

    /**
     * Update lastseen value of given user
     * @param user user
     * @implNote this has to be called only if user belongs to chat
     */
    public void updateLastSeen(User user) {
        if (user.equals(user1)) lastSeen1 = new Timestamp(System.currentTimeMillis());
        else lastSeen2 = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Update lastmessage value
     * @implNote this has to be called only if user belongs to chat
     */
    public void updateLastMessage() {
        lastMessage = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Timestamp getLastSeen1() {
        return lastSeen1;
    }

    public void setLastSeen1(Timestamp lastSeen1) {
        this.lastSeen1 = lastSeen1;
    }

    public Timestamp getLastSeen2() {
        return lastSeen2;
    }

    public void setLastSeen2(Timestamp lastSeen2) {
        this.lastSeen2 = lastSeen2;
    }

    public Timestamp getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Timestamp lastMessage) {
        this.lastMessage = lastMessage;
    }
}
