package com.socatel.rest_api.sdi.tweet;

import java.util.LinkedList;
import java.util.List;

public class TweetsList {
    private List<Tweet> postsByTopics;

    public TweetsList() {
        postsByTopics = new LinkedList<>();
    }

    public List<Tweet> getPostsByTopics() {
        return postsByTopics;
    }

    public void setPostsByTopics(List<Tweet> postsByTopics) {
        this.postsByTopics = postsByTopics;
    }
}
