package com.socatel.rest_api.rdi;

public class GroupPopularity {
    private Integer group_id;
    private Double popularity_ratio;

    public GroupPopularity() {}

    public GroupPopularity(Integer group_id, Double popularity_ratio) {
        this.group_id = group_id;
        this.popularity_ratio = popularity_ratio;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Double getPopularity_ratio() {
        return popularity_ratio;
    }

    public void setPopularity_ratio(Double popularity_ratio) {
        this.popularity_ratio = popularity_ratio;
    }

    @Override
    public String toString() {
        return "GroupPopularity{" +
                "group_id=" + group_id +
                ", popularity_ratio=" + popularity_ratio +
                '}';
    }
}
