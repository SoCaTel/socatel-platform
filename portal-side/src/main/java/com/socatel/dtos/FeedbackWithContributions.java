package com.socatel.dtos;

import com.socatel.models.Answer;
import com.socatel.models.Document;
import com.socatel.models.Feedback;
import com.socatel.models.Post;

import java.util.List;

public class FeedbackWithContributions {

    private Feedback feedback;
    private List<Post> posts;
    private Integer upvotes;
    private Integer downvotes;
    private List<Document> documents;
    private List<Answer> answers;

    public FeedbackWithContributions() {

    }

    public FeedbackWithContributions(Feedback feedback, List<Post> posts, List<Document> documents) {
        this.feedback = feedback;
        this.posts = posts;
        this.documents = documents;
    }

    public FeedbackWithContributions(List<Answer> answers, Feedback feedback,  List<Document> documents) {
        this.feedback = feedback;
        this.answers = answers;
        this.documents = documents;
    }

    public FeedbackWithContributions(Feedback feedback, Integer upvotes, Integer downvotes, List<Document> documents) {
        this.feedback = feedback;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.documents = documents;
    }

    public boolean hasUpVoted(String username) {
        return feedback.hasUpVoted(username);
    }

    public boolean hasDownVoted(String username) {
        return feedback.hasDownVoted(username);
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
