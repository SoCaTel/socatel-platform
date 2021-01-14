package com.socatel.services.answer;

import com.socatel.models.Answer;
import com.socatel.models.Feedback;
import com.socatel.models.Question;

import java.util.List;

public interface AnswerService {
    Answer save(Answer answer);

    List<Answer> findByQuestion(Question question);

    Answer findById(Integer id);

    List<Answer> findByFeedback(Feedback feedback);
}
