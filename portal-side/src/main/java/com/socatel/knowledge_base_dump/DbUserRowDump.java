package com.socatel.knowledge_base_dump;

import com.socatel.dtos.knowledge_base.GroupIdDTO;
import com.socatel.dtos.knowledge_base.SkillIdDTO;
import com.socatel.dtos.knowledge_base.ThemeIdDTO;
import com.socatel.models.Language;
import com.socatel.models.Locality;

import java.util.List;

public class DbUserRowDump {
    private String user_id;
    private DbLocalityRowDump locality;
    private DbLanguageRowDump primary_language;
    private DbLanguageRowDump secondary_language;
    private Integer organisation_id;
    private List<ThemeIdDTO> themes;
    private List<GroupIdDTO> groups;
    private List<SkillIdDTO> skills;

    public DbUserRowDump() {}

    public DbUserRowDump(String userId, Locality locality, Language primaryLanguage, Language secondaryLanguage, Integer organisationId, List<ThemeIdDTO> themes, List<GroupIdDTO> groups, List<SkillIdDTO> skills) {
        this.user_id = userId;
        this.locality = new DbLocalityRowDump(locality);
        this.primary_language = new DbLanguageRowDump(primaryLanguage);
        this.secondary_language = secondaryLanguage == null ? null : new DbLanguageRowDump(secondaryLanguage);
        this.organisation_id = organisationId;
        this.themes = themes;
        this.groups = groups;
        this.skills = skills;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public DbLocalityRowDump getLocality() {
        return locality;
    }

    public void setLocality(DbLocalityRowDump locality) {
        this.locality = locality;
    }

    public DbLanguageRowDump getPrimary_language() {
        return primary_language;
    }

    public void setPrimary_language(DbLanguageRowDump primary_language) {
        this.primary_language = primary_language;
    }

    public DbLanguageRowDump getSecondary_language() {
        return secondary_language;
    }

    public void setSecondary_language(DbLanguageRowDump secondary_language) {
        this.secondary_language = secondary_language;
    }

    public Integer getOrganisation_id() {
        return organisation_id;
    }

    public void setOrganisation_id(Integer organisation_id) {
        this.organisation_id = organisation_id;
    }

    public List<ThemeIdDTO> getThemes() {
        return themes;
    }

    public void setThemes(List<ThemeIdDTO> themes) {
        this.themes = themes;
    }

    public List<GroupIdDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupIdDTO> groups) {
        this.groups = groups;
    }

    public List<SkillIdDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillIdDTO> skills) {
        this.skills = skills;
    }
}
