package com.socatel.rest_api.rdi;

public class GroupTrending {
    private Integer group_id;
    private Double trend_score;

    public GroupTrending() {}

    public GroupTrending(Integer group_id, Double trend_score) {
        this.group_id = group_id;
        this.trend_score = trend_score;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Double getTrend_score() {
        return trend_score;
    }

    public void setTrend_score(Double trend_score) {
        this.trend_score = trend_score;
    }

    @Override
    public String toString() {
        return "GroupTrending{" +
                "group_id=" + group_id +
                ", trend_score=" + trend_score +
                '}';
    }
}
