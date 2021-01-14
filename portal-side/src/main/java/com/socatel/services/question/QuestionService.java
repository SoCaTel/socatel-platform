package com.socatel.services.question;

import com.socatel.models.Group;
import com.socatel.models.Question;
import com.socatel.utils.enums.QuestionEnum;

import java.util.List;

public interface QuestionService {
    Question save(Question question);

    List<Question> findByGroupAndType(Group group, QuestionEnum type);

    boolean groupHasQuestions(Group group);
}
