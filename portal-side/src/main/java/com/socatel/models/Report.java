package com.socatel.models;

import com.socatel.utils.enums.ReportReason;
import com.socatel.utils.enums.ReportStatus;

import javax.persistence.*;

@Entity
@Table(name = "so_report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private int id;

    @Column(name = "report_text")
    private String text;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "report_reason")
    private ReportReason reason;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "report_status")
    private ReportStatus status;

    @ManyToOne
    @JoinColumn(name = "accuser_id", nullable = false)
    private User accuser;

    @ManyToOne
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name="proposition_id")
    private Proposition proposition;

    public Report() {}

    public Report(String text, ReportReason reason, User accuser, User reported, Post post) {
        this.text = text;
        this.reason = reason;
        this.accuser = accuser;
        this.reported = reported;
        this.post = post;
    }

    public Report(String text, ReportReason reason, User accuser, User reported, Proposition proposition) {
        this.text = text;
        this.reason = reason;
        this.accuser = accuser;
        this.reported = reported;
        this.proposition = proposition;
    }

    public boolean isPost() {
        return post != null;
    }

    public boolean isProposition() {
        return proposition != null;
    }

    public String getContributionText() {
        if (isPost()) return post.getText();
        return proposition.getText();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public User getAccuser() {
        return accuser;
    }

    public void setAccuser(User accuser) {
        this.accuser = accuser;
    }

    public User getReported() {
        return reported;
    }

    public void setReported(User reported) {
        this.reported = reported;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
}
