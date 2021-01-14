package com.socatel.knowledge_base_dump;

import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.VisibleEnum;

import java.sql.Timestamp;

public class DbHistoryRowDump {
    private Integer history_id;
    private String history_text;
    private Timestamp history_timestamp;
    private Integer history_type;
    private Integer history_level;
    private String user_id;
    private Integer organisation_id;
    private Integer group_id;

    public DbHistoryRowDump(Integer id, String text, Timestamp timestamp, HistoryTypeEnum type, VisibleEnum level, String userId, Integer organisationId, Integer groupId) {
        this.history_id = id;
        this.history_text = text;
        this.history_timestamp = timestamp;
        this.history_type = type.ordinal();
        this.user_id = userId;
        this.organisation_id = organisationId;
        this.group_id = groupId;
        this.history_level = level.ordinal();
    }

    public Integer getHistory_id() {
        return history_id;
    }

    public void setHistory_id(Integer history_id) {
        this.history_id = history_id;
    }

    public String getHistory_text() {
        return history_text;
    }

    public void setHistory_text(String history_text) {
        this.history_text = history_text;
    }

    public Timestamp getHistory_timestamp() {
        return history_timestamp;
    }

    public void setHistory_timestamp(Timestamp history_timestamp) {
        this.history_timestamp = history_timestamp;
    }

    public Integer getHistory_type() {
        return history_type;
    }

    public void setHistory_type(Integer history_type) {
        this.history_type = history_type;
    }

    public Integer getHistory_level() {
        return history_level;
    }

    public void setHistory_level(Integer history_level) {
        this.history_level = history_level;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getOrganisation_id() {
        return organisation_id;
    }

    public void setOrganisation_id(Integer organisation_id) {
        this.organisation_id = organisation_id;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }
}
