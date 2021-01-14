package com.socatel.models.keys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserFeedbackKey implements Serializable {
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "feedback_id")
    private Integer feedbackId;

    public UserFeedbackKey() {

    }

    public UserFeedbackKey(Integer userId, Integer feedbackId) {
        this.userId = userId;
        this.feedbackId = feedbackId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFeedbackKey that = (UserFeedbackKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(feedbackId, that.feedbackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, feedbackId);
    }
}
