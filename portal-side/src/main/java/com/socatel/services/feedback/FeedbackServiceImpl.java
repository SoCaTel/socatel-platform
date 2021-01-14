package com.socatel.services.feedback;

import com.socatel.models.Feedback;
import com.socatel.repositories.FeedbackRepository;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService{

    private FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public List<Feedback> findFeedbackByIdeaId(Integer ideaId) {
        return feedbackRepository.findAllByPostId(ideaId, Constants.SORT_BY_TIMESTAMP_DESC);
    }

    @Override
    public List<Feedback> findFeedbackByIdeaIdAndVisible(Integer ideaId, VisibleEnum visible) {
        return feedbackRepository.findAllByPostIdAndVisible(ideaId, visible, Constants.SORT_BY_TIMESTAMP_DESC);
    }

    @Override
    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback findFeedbackById(Integer feedbackId) {
        return feedbackRepository.findById(feedbackId).orElse(null);
    }
}
