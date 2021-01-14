package com.socatel.rest_api.rdi;

public class GroupViewsJoins {
    private Integer group_id;
    private Integer total_views_and_subs;
    private Integer total_views;
    private Integer total_joins;

    public GroupViewsJoins() {}

    public GroupViewsJoins(Integer group_id, Integer total_views_and_subs, Integer total_views, Integer total_joins) {
        this.group_id = group_id;
        this.total_views_and_subs = total_views_and_subs;
        this.total_views = total_views;
        this.total_joins = total_joins;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Integer getTotal_views_and_subs() {
        return total_views_and_subs;
    }

    public void setTotal_views_and_subs(Integer total_views_and_subs) {
        this.total_views_and_subs = total_views_and_subs;
    }

    public Integer getTotal_views() {
        return total_views;
    }

    public void setTotal_views(Integer total_views) {
        this.total_views = total_views;
    }

    public Integer getTotal_joins() {
        return total_joins;
    }

    public void setTotal_joins(Integer total_joins) {
        this.total_joins = total_joins;
    }

    @Override
    public String toString() {
        return "GroupViewsJoins{" +
                "group_id=" + group_id +
                ", total_views_and_subs=" + total_views_and_subs +
                ", total_views=" + total_views +
                ", total_joins=" + total_joins +
                '}';
    }
}
