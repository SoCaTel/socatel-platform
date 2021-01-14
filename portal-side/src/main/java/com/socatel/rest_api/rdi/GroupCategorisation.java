package com.socatel.rest_api.rdi;

public class GroupCategorisation {
    private Integer theme_id;
    private Integer total_groups;

    public GroupCategorisation() {}

    public GroupCategorisation(Integer theme_id, Integer total_groups) {
        this.theme_id = theme_id;
        this.total_groups = total_groups;
    }

    public Integer getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(Integer theme_id) {
        this.theme_id = theme_id;
    }

    public Integer getTotal_groups() {
        return total_groups;
    }

    public void setTotal_groups(Integer total_groups) {
        this.total_groups = total_groups;
    }

    @Override
    public String toString() {
        return "GroupCategorisation{" +
                "theme_id=" + theme_id +
                ", total_groups=" + total_groups +
                '}';
    }
}
