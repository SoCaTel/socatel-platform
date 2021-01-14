package com.socatel.models;

import com.socatel.models.relationships.UserFeedbackVote;
import com.socatel.utils.enums.ByOrgEnum;
import com.socatel.utils.enums.VisibleEnum;
import com.socatel.utils.enums.VoteTypeEnum;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "so_feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private int id;

    @Column(name = "feedback_title")
    private String title;

    @Column(name = "feedback_description")
    private String description;

    @Column(name = "feedback_question")
    private String question;

    @Column(name = "feedback_visible")
    @Enumerated(EnumType.ORDINAL)
    private VisibleEnum visible;

    @Column(name = "feedback_timestamp")
    private Timestamp timestamp;

    @Column(name = "feedback_by_org")
    @Enumerated(EnumType.ORDINAL)
    private ByOrgEnum byOrg;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "feedback", fetch = FetchType.LAZY)
    private Set<UserFeedbackVote> usersLikes;

    public Feedback() {

    }

    public Feedback(String title, String description, String question, ByOrgEnum byOrg, Group group, Post post, User user) {
        this.title = title;
        this.description = description;
        this.question = question;
        this.byOrg = byOrg;
        this.group = group;
        this.post = post;
        this.user = user;
    }

    public boolean isByOrg() {
        return byOrg.equals(ByOrgEnum.BY_ORG);
    }

    public boolean hasUpVoted(String username) {
        if (usersLikes != null)
            for (UserFeedbackVote vote : usersLikes)
                if (vote.getVoteType().equals(VoteTypeEnum.UP_VOTED) && vote.getUser().getUsername().equals(username))
                    return true;
        return false;
    }

    public boolean hasDownVoted(String username) {
        if (usersLikes != null)
            for (UserFeedbackVote vote : usersLikes)
                if (vote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED) && vote.getUser().getUsername().equals(username))
                    return true;
        return false;
    }

    public boolean isVisible() {
        return visible.equals(VisibleEnum.VISIBLE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public VisibleEnum getVisible() {
        return visible;
    }

    public void setVisible(VisibleEnum visible) {
        this.visible = visible;
    }

    public ByOrgEnum getByOrg() {
        return byOrg;
    }

    public void setByOrg(ByOrgEnum byOrg) {
        this.byOrg = byOrg;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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

    public Set<UserFeedbackVote> getUsersLikes() {
        return usersLikes;
    }

    public void setUsersLikes(Set<UserFeedbackVote> usersLikes) {
        this.usersLikes = usersLikes;
    }
}
