package com.socatel.repositories;

import com.socatel.models.Answer;
import com.socatel.models.Feedback;
import com.socatel.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer,Integer> {
    List<Answer> findAllByQuestion(Question question);
    List<Answer> findAllByFeedback(Feedback feedback);
}
