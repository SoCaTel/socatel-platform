package com.socatel.rest_api.rdi.demographic;

public class GroupDemographic {
    private Demographic skills_demographics;
    private Demographic primary_lang_demographics;
    private Demographic locality_demographics;
    private Demographic sec_lang_demographics;
    private Demographic themes_demographics;
    private Demographic locality_parent_demographics;

    public GroupDemographic() {}

    public GroupDemographic(Demographic skills_demographics, Demographic primary_lang_demographics,
                            Demographic locality_demographics, Demographic sec_lang_demographics,
                            Demographic themes_demographics, Demographic locality_parent_demographics) {
        this.skills_demographics = skills_demographics;
        this.primary_lang_demographics = primary_lang_demographics;
        this.locality_demographics = locality_demographics;
        this.sec_lang_demographics = sec_lang_demographics;
        this.themes_demographics = themes_demographics;
        this.locality_parent_demographics = locality_parent_demographics;
    }

    public Demographic getSkills_demographics() {
        return skills_demographics;
    }

    public void setSkills_demographics(Demographic skills_demographics) {
        this.skills_demographics = skills_demographics;
    }

    public Demographic getPrimary_lang_demographics() {
        return primary_lang_demographics;
    }

    public void setPrimary_lang_demographics(Demographic primary_lang_demographics) {
        this.primary_lang_demographics = primary_lang_demographics;
    }

    public Demographic getLocality_demographics() {
        return locality_demographics;
    }

    public void setLocality_demographics(Demographic locality_demographics) {
        this.locality_demographics = locality_demographics;
    }

    public Demographic getSec_lang_demographics() {
        return sec_lang_demographics;
    }

    public void setSec_lang_demographics(Demographic sec_lang_demographics) {
        this.sec_lang_demographics = sec_lang_demographics;
    }

    public Demographic getThemes_demographics() {
        return themes_demographics;
    }

    public void setThemes_demographics(Demographic themes_demographics) {
        this.themes_demographics = themes_demographics;
    }

    public Demographic getLocality_parent_demographics() {
        return locality_parent_demographics;
    }

    public void setLocality_parent_demographics(Demographic locality_parent_demographics) {
        this.locality_parent_demographics = locality_parent_demographics;
    }

    @Override
    public String toString() {
        return "GroupDemographic{" +
                "skills_demographics=" + skills_demographics +
                ", primary_lang_demographics=" + primary_lang_demographics +
                ", locality_demographics=" + locality_demographics +
                ", sec_lang_demographics=" + sec_lang_demographics +
                ", themes_demographics=" + themes_demographics +
                ", locality_parent_demographics=" + locality_parent_demographics +
                '}';
    }
}
