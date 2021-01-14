package com.socatel.dtos;

import com.socatel.models.Answer;
import com.socatel.models.Question;

import java.util.List;

public class QuestionAndAnswers {
    private Question question;
    private List<Answer> answers;

    public QuestionAndAnswers(Question question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

}
