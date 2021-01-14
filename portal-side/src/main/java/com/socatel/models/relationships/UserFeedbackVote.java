package com.socatel.models.relationships;

import com.socatel.models.Feedback;
import com.socatel.models.User;
import com.socatel.models.keys.UserFeedbackKey;
import com.socatel.utils.enums.VoteTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "so_user_feedback_vote")
@IdClass(UserFeedbackKey.class)
public class UserFeedbackVote {

    @Id
    @Column(name = "feedback_id")
    private int feedbackId;

    @Id
    @Column(name = "user_id")
    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("feedback_id")
    @JoinColumn(name="feedback_id")
    private Feedback feedback;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_feedback_vote_type")
    private VoteTypeEnum voteType;

    public UserFeedbackVote() {

    }

    public UserFeedbackVote(User user, Feedback feedback) {
        this.user = user;
        this.feedback = feedback;
        userId = user.getId();
        feedbackId = feedback.getId();
        voteType = VoteTypeEnum.NO_VOTED;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public VoteTypeEnum getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteTypeEnum voteType) {
        this.voteType = voteType;
    }
}
