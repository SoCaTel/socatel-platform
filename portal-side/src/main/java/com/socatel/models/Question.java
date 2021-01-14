package com.socatel.models;

import com.socatel.utils.enums.QuestionEnum;

import javax.persistence.*;

@Entity
@Table(name = "so_question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private int id;

    @Column(name = "question_text")
    private String text;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "question_type")
    private QuestionEnum type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private Group group;

    public Question() {}

    public Question(String text, QuestionEnum type, Group group) {
        this.text = text;
        this.type = type;
        this.group = group;
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

    public QuestionEnum getType() {
        return type;
    }

    public void setType(QuestionEnum type) {
        this.type = type;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
