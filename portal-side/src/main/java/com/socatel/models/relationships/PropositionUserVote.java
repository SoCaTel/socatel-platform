package com.socatel.models.relationships;

import com.socatel.models.Proposition;
import com.socatel.models.User;
import com.socatel.models.keys.PropositionUserKey;
import com.socatel.utils.enums.VoteTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "so_proposition_user_vote")
@IdClass(PropositionUserKey.class)
public class PropositionUserVote {
    @Id
    @Column(name = "post_id")
    private int propositionId;

    @Id
    @Column(name = "user_id")
    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("post_id")
    @JoinColumn(name = "proposition_id")
    private Proposition proposition;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "proposition_user_vote_type")
    private VoteTypeEnum voteType;

    public PropositionUserVote() {}

    public PropositionUserVote(User user, Proposition proposition) {
        this.user = user;
        this.proposition = proposition;
        userId = user.getId();
        propositionId = proposition.getId();
        voteType = VoteTypeEnum.NO_VOTED;
    }

    public int getPropositionId() {
        return propositionId;
    }

    public void setPropositionId(int propositionId) {
        this.propositionId = propositionId;
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

    public Proposition getProposition() {
        return proposition;
    }

    public void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    public VoteTypeEnum getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteTypeEnum voteType) {
        this.voteType = voteType;
    }
}
