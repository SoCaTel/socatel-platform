package com.socatel.services.question;

import com.socatel.models.Group;
import com.socatel.models.Question;
import com.socatel.repositories.QuestionRepository;
import com.socatel.utils.enums.QuestionEnum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question save(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> findByGroupAndType(Group group, QuestionEnum type) {
        return questionRepository.findAllByGroupAndType(group, type);
    }

    @Override
    public boolean groupHasQuestions(Group group) {
        return questionRepository.countByGroup(group) > 0;
    }
}
