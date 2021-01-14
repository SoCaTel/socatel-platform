package com.socatel.dtos.knowledge_base;

public class ThemeIdDTO {

    private int theme_id;
    private String theme_name;

    public ThemeIdDTO() {}

    public ThemeIdDTO(Integer id, String name) {
        this.theme_id = id;
        this.theme_name = name;
    }

    public int getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(int theme_id) {
        this.theme_id = theme_id;
    }

    public String getTheme_name() {
        return theme_name;
    }

    public void setTheme_name(String theme_name) {
        this.theme_name = theme_name;
    }
}
