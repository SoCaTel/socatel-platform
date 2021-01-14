package com.socatel.knowledge_base_dump;

import com.socatel.utils.enums.VoteTypeEnum;

public class DbUserPropositionVoteRowDump {
    private String id;
    private String user_id;
    private Integer proposition_id;
    private Integer proposition_user_vote_type;

    public DbUserPropositionVoteRowDump(String userId, Integer propositionId, VoteTypeEnum vote) {
        id = userId + "-" + propositionId;
        this.user_id = userId;
        this.proposition_id = propositionId;
        this.proposition_user_vote_type = vote.ordinal();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getProposition_id() {
        return proposition_id;
    }

    public void setProposition_id(Integer proposition_id) {
        this.proposition_id = proposition_id;
    }

    public Integer getProposition_user_vote_type() {
        return proposition_user_vote_type;
    }

    public void setProposition_user_vote_type(Integer proposition_user_vote_type) {
        this.proposition_user_vote_type = proposition_user_vote_type;
    }
}
