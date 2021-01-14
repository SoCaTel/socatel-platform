package com.socatel.services.feedback;

import com.socatel.models.Feedback;
import com.socatel.utils.enums.VisibleEnum;

import java.util.List;

public interface FeedbackService {
    List<Feedback> findFeedbackByIdeaId(Integer ideaId);

    List<Feedback> findFeedbackByIdeaIdAndVisible(Integer ideaId, VisibleEnum visible);

    Feedback save(Feedback feedback);
    Feedback findFeedbackById(Integer feedbackId);
}
