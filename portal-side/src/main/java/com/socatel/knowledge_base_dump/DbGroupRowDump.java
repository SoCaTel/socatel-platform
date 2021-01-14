package com.socatel.knowledge_base_dump;

import com.socatel.dtos.knowledge_base.ThemeIdDTO;
import com.socatel.models.Language;
import com.socatel.models.Locality;
import com.socatel.utils.enums.GroupStatusEnum;

import java.sql.Timestamp;
import java.util.List;

public class DbGroupRowDump {
    private Integer group_id;
    private String group_name;
    private String group_description;
    private Integer group_status;
    private Timestamp group_create_time;
    private Timestamp group_next_step_time;
    private DbLocalityRowDump locality;
    private DbLanguageRowDump language;
    private String user_initiator_id;
    private List<ThemeIdDTO> themes;
    private List<String> users;

    public DbGroupRowDump(Integer groupId, String name, String description, GroupStatusEnum status, Timestamp timestamp, Timestamp nextStepTimestamp, Locality locality, Language language, String userId, List<ThemeIdDTO> themes, List<String> users) {
        this.group_id = groupId;
        this.group_name = name;
        this.group_description = description;
        this.group_status = status.ordinal();
        this.group_create_time = timestamp;
        this.group_next_step_time = nextStepTimestamp;
        this.locality = new DbLocalityRowDump(locality);
        this.language = new DbLanguageRowDump(language);
        this.user_initiator_id = userId;
        this.themes = themes;
        this.users = users;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_description() {
        return group_description;
    }

    public void setGroup_description(String group_description) {
        this.group_description = group_description;
    }

    public Integer getGroup_status() {
        return group_status;
    }

    public void setGroup_status(Integer group_status) {
        this.group_status = group_status;
    }

    public Timestamp getGroup_create_time() {
        return group_create_time;
    }

    public void setGroup_create_time(Timestamp group_create_time) {
        this.group_create_time = group_create_time;
    }

    public Timestamp getGroup_next_step_time() {
        return group_next_step_time;
    }

    public void setGroup_next_step_time(Timestamp group_next_step_time) {
        this.group_next_step_time = group_next_step_time;
    }

    public DbLocalityRowDump getLocality() {
        return locality;
    }

    public void setLocality(DbLocalityRowDump locality) {
        this.locality = locality;
    }

    public DbLanguageRowDump getLanguage() {
        return language;
    }

    public void setLanguage(DbLanguageRowDump language) {
        this.language = language;
    }

    public String getUser_initiator_id() {
        return user_initiator_id;
    }

    public void setUser_initiator_id(String user_initiator_id) {
        this.user_initiator_id = user_initiator_id;
    }

    public List<ThemeIdDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<ThemeIdDTO> themes) {
        this.themes = themes;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

}
