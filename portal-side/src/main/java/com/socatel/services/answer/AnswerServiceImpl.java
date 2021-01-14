package com.socatel.services.answer;

import com.socatel.models.Answer;
import com.socatel.models.Feedback;
import com.socatel.models.Question;
import com.socatel.repositories.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public List<Answer> findByQuestion(Question question) {
        List<Answer> answers = answerRepository.findAllByQuestion(question);
        for (Answer a: answers) {
            a.setVotes(a.getUsersVotes().size());
        }
        return answers;
    }

    @Override
    public Answer findById(Integer id) {
        return answerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Answer> findByFeedback(Feedback feedback) {
        List<Answer> answers = answerRepository.findAllByFeedback(feedback);
        for (Answer a: answers) {
            a.setVotes(a.getUsersVotes().size());
        }
        return answers;
    }
}
