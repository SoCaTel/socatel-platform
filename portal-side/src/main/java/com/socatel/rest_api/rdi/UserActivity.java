package com.socatel.rest_api.rdi;

public class UserActivity {
    private String user_id;
    private String user_views;
    private String user_posts;

    public UserActivity() {}

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_views() {
        return user_views;
    }

    public void setUser_views(String user_views) {
        this.user_views = user_views;
    }

    public String getUser_posts() {
        return user_posts;
    }

    public void setUser_posts(String user_posts) {
        this.user_posts = user_posts;
    }
}
