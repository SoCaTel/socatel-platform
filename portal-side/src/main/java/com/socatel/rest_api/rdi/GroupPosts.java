package com.socatel.rest_api.rdi;

public class GroupPosts {
    private Integer group_id;
    private Integer no_of_posts;

    public GroupPosts() {}

    public GroupPosts(Integer group_id, Integer no_of_posts) {
        this.group_id = group_id;
        this.no_of_posts = no_of_posts;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Integer getNo_of_posts() {
        return no_of_posts;
    }

    public void setNo_of_posts(Integer no_of_posts) {
        this.no_of_posts = no_of_posts;
    }

    @Override
    public String toString() {
        return "GroupPosts{" +
                "group_id=" + group_id +
                ", no_of_posts=" + no_of_posts +
                '}';
    }
}
