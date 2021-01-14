package com.socatel.models.keys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PropositionUserKey implements Serializable {
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "proposition_id")
    private Integer propositionId;

    public PropositionUserKey() {}

    public PropositionUserKey(Integer userId, Integer propositionId) {
        this.userId = userId;
        this.propositionId = propositionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPropositionId() {
        return propositionId;
    }

    public void setPropositionId(Integer propositionId) {
        this.propositionId = propositionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropositionUserKey that = (PropositionUserKey) o;
        return userId.equals(that.userId) &&
                propositionId.equals(that.propositionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, propositionId);
    }
}
