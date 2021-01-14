package com.socatel.rest_api.rec;

public class GroupScore {
    private Integer group_id;
    private Double score;

    public GroupScore() {}

    public GroupScore(Integer group_id, Double score) {
        this.group_id = group_id;
        this.score = score;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "GroupScore{" +
                "group_id='" + group_id + '\'' +
                ", score=" + score +
                '}';
    }
}
