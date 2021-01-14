package com.socatel.knowledge_base_dump;

import com.socatel.utils.enums.PinEnum;
import com.socatel.utils.enums.PostPhaseEnum;
import com.socatel.utils.enums.PostTypeEnum;
import com.socatel.utils.enums.VisibleEnum;

import java.sql.Timestamp;

public class DbPostRowDump {
    private Integer post_id;
    private String post_text;
    private Timestamp post_timestamp;
    private Integer post_upvotes;
    private Integer post_downvotes;
    private Integer post_type;
    private Integer post_phase;
    private Integer post_visible;
    private Integer post_pin;
    private Integer post_parent_id;
    private String author_user_id;
    private Integer group_id;
    private Integer organisation_id;

    public DbPostRowDump(Integer postId, String text, Timestamp timestamp, Integer upvotes, Integer downvotes, PostTypeEnum type, PostPhaseEnum phase, VisibleEnum visible, PinEnum pin, Integer postParentId, String userId, Integer groupId, Integer organisationId) {
        this.post_id = postId;
        this.post_text = text;
        this.post_timestamp = timestamp;
        this.post_upvotes = upvotes;
        this.post_downvotes = downvotes;
        this.post_type = type==null?null:type.ordinal();
        this.post_phase = phase.ordinal();
        this.post_visible = visible.ordinal();
        this.post_pin = pin.ordinal();
        this.post_parent_id = postParentId;
        this.author_user_id = userId;
        this.group_id = groupId;
        this.organisation_id = organisationId;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public Timestamp getPost_timestamp() {
        return post_timestamp;
    }

    public void setPost_timestamp(Timestamp post_timestamp) {
        this.post_timestamp = post_timestamp;
    }

    public Integer getPost_upvotes() {
        return post_upvotes;
    }

    public void setPost_upvotes(Integer post_upvotes) {
        this.post_upvotes = post_upvotes;
    }

    public Integer getPost_downvotes() {
        return post_downvotes;
    }

    public void setPost_downvotes(Integer post_downvotes) {
        this.post_downvotes = post_downvotes;
    }

    public Integer getPost_type() {
        return post_type;
    }

    public void setPost_type(Integer post_type) {
        this.post_type = post_type;
    }

    public Integer getPost_phase() {
        return post_phase;
    }

    public void setPost_phase(Integer post_phase) {
        this.post_phase = post_phase;
    }

    public Integer getPost_visible() {
        return post_visible;
    }

    public void setPost_visible(Integer post_visible) {
        this.post_visible = post_visible;
    }

    public Integer getPost_pin() {
        return post_pin;
    }

    public void setPost_pin(Integer post_pin) {
        this.post_pin = post_pin;
    }

    public Integer getPost_parent_id() {
        return post_parent_id;
    }

    public void setPost_parent_id(Integer post_parent_id) {
        this.post_parent_id = post_parent_id;
    }

    public String getAuthor_user_id() {
        return author_user_id;
    }

    public void setAuthor_user_id(String author_user_id) {
        this.author_user_id = author_user_id;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Integer getOrganisation_id() {
        return organisation_id;
    }

    public void setOrganisation_id(Integer organisation_id) {
        this.organisation_id = organisation_id;
    }
}
