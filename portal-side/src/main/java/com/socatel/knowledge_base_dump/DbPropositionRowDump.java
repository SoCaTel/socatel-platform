package com.socatel.knowledge_base_dump;

import com.socatel.utils.enums.PinEnum;
import com.socatel.utils.enums.PropositionTypeEnum;
import com.socatel.utils.enums.VisibleEnum;

import java.sql.Timestamp;

public class DbPropositionRowDump {
    private Integer proposition_id;
    private String proposition_text;
    private Integer proposition_type;
    private Integer proposition_upvotes;
    private Integer proposition_downvotes;
    private Integer proposition_visible;
    private Integer proposition_pin;
    private Timestamp proposition_timestamp;
    private Integer post_id;
    private String user_id;

    public DbPropositionRowDump(Integer propositionId, String text, PropositionTypeEnum type, Integer upvotes, Integer downvotes, VisibleEnum visible, PinEnum pin, Timestamp timestamp, Integer postId, String userId) {
        this.proposition_id = propositionId;
        this.proposition_text = text;
        this.proposition_type = type == null ? null : type.ordinal();
        this.proposition_upvotes = upvotes;
        this.proposition_downvotes = downvotes;
        this.proposition_visible = visible.ordinal();
        this.proposition_pin = pin.ordinal();
        this.proposition_timestamp = timestamp;
        this.post_id = postId;
        this.user_id = userId;
    }

    public Integer getProposition_id() {
        return proposition_id;
    }

    public void setProposition_id(Integer proposition_id) {
        this.proposition_id = proposition_id;
    }

    public String getProposition_text() {
        return proposition_text;
    }

    public void setProposition_text(String proposition_text) {
        this.proposition_text = proposition_text;
    }

    public Integer getProposition_type() {
        return proposition_type;
    }

    public void setProposition_type(Integer proposition_type) {
        this.proposition_type = proposition_type;
    }

    public Integer getProposition_upvotes() {
        return proposition_upvotes;
    }

    public void setProposition_upvotes(Integer proposition_upvotes) {
        this.proposition_upvotes = proposition_upvotes;
    }

    public Integer getProposition_downvotes() {
        return proposition_downvotes;
    }

    public void setProposition_downvotes(Integer proposition_downvotes) {
        this.proposition_downvotes = proposition_downvotes;
    }

    public Integer getProposition_visible() {
        return proposition_visible;
    }

    public void setProposition_visible(Integer proposition_visible) {
        this.proposition_visible = proposition_visible;
    }

    public Integer getProposition_pin() {
        return proposition_pin;
    }

    public void setProposition_pin(Integer proposition_pin) {
        this.proposition_pin = proposition_pin;
    }

    public Timestamp getProposition_timestamp() {
        return proposition_timestamp;
    }

    public void setProposition_timestamp(Timestamp proposition_timestamp) {
        this.proposition_timestamp = proposition_timestamp;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
