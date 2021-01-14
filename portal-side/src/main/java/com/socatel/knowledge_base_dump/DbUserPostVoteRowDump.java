package com.socatel.knowledge_base_dump;

import com.socatel.utils.enums.VoteTypeEnum;

public class DbUserPostVoteRowDump {
    private String id;
    private String user_id;
    private Integer post_id;
    private Integer user_post_vote_type;

    public DbUserPostVoteRowDump(String userId, Integer postId, VoteTypeEnum vote) {
        this.user_id = userId;
        this.post_id = postId;
        this.user_post_vote_type = vote.ordinal();
        id = userId + "-" + postId;
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

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public Integer getUser_post_vote_type() {
        return user_post_vote_type;
    }

    public void setUser_post_vote_type(Integer user_post_vote_type) {
        this.user_post_vote_type = user_post_vote_type;
    }
}
