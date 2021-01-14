package com.socatel.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "so_document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private int id;

    @Size(min = 1, max = 255, message = "{document_name.length}")
    @Column(name = "document_name")
    private String name;

    @Size(min = 1, max = 255, message = "{document_name.length}")
    @Column(name = "document_type")
    private String type;

    @Column(name = "document_bytes")
    private Integer bytes;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "proposition_id")
    private Proposition proposition;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    public Document() {}

    public Document(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
    }

    public Document(String name, String type, byte[] data, Group group) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
        this.group = group;
    }

    public Document(String name, String type, byte[] data, Post post) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
        this.post = post;
    }

    public Document(String name, String type, byte[] data, Group group, Post post) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
        this.group = group;
        this.post = post;
    }

    public Document(String name, String type, byte[] data, Service service) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
        this.service = service;
    }

    public Document(String name, String type, byte[] data, Message message) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
        this.message = message;
    }

    public Document(String name, String type, byte[] data, Proposition proposition) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
        this.proposition = proposition;
    }

    public Document(String name, String type, byte[] data, Group group, Feedback feedback) {
        this.name = name;
        this.type = type;
        this.bytes = data.length;
        this.feedback = feedback;
    }

    public String size() {
        int unit = 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = ("kMGTPE").charAt(exp-1);
        return String.format("%.2f %cB", bytes / Math.pow(unit, exp), pre);
    }

    public User getUser() {
        if (post != null) return post.getAuthor();
        if (group != null) return group.getInitiator();
        return message.getUser();
    }

    public String getCssClass() {
        if (type.contains("image")) return "fa-file-image";
        if (type.contains("word")) return "fa-file-word";
        if (type.contains("excel")) return "fa-file-excel";
        if (type.contains("presentation")) return "fa-file-powerpoint";
        if (type.contains("pdf")) return "fa-file-pdf";
        if (type.contains("video")) return "fa-file-video";
        if (type.contains("audio")) return "fa-file-audio";
        return "fa-file-alt";
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Proposition getProposition() {
        return proposition;
    }

    public void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Integer getBytes() {
        return bytes;
    }

    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
