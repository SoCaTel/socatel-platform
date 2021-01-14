package com.socatel.knowledge_base_dump;

import com.socatel.models.Language;

public class DbLanguageRowDump {
    private Integer language_id;
    private String language_code;
    private String language_name;

    public DbLanguageRowDump() {
        super();
    }

    public DbLanguageRowDump(Language language) {
        language_id = language.getId();
        language_code = language.getCode();
        language_name = language.getName();
    }

    public Integer getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(Integer language_id) {
        this.language_id = language_id;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }
}
