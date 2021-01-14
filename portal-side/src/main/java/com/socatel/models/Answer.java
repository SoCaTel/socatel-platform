package com.socatel.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "so_answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private int id;

    @Column(name = "answer_text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    @ManyToMany(mappedBy = "votedAnswers", fetch = FetchType.LAZY)
    private Set<User> usersVotes;

    @Transient
    private Integer votes;

    public Answer() {
        usersVotes = new HashSet<>();
    }

    public Answer(String text, Feedback feedback) {
        this.text = text;
        this.feedback = feedback;
        usersVotes = new HashSet<>();
    }

    public Answer(String text, Question question) {
        this.text = text;
        this.question = question;
        usersVotes = new HashSet<>();
    }

    public Answer(String text, Question question, Integer votes) {
        this.text = text;
        this.question = question;
        this.votes = votes;
        usersVotes = new HashSet<>();
    }

    public void addUser(User user) {
        usersVotes.add(user);
    }

    public void removeUser(User user) {
        usersVotes.remove(user);
    }

    public boolean hasVoted(User user) {
        return usersVotes.contains(user);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Set<User> getUsersVotes() {
        return usersVotes;
    }

    public void setUsersVotes(Set<User> likes) {
        this.usersVotes = likes;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}
