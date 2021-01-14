package com.socatel.services.proposition;

import com.socatel.models.Feedback;
import com.socatel.models.Proposition;
import com.socatel.utils.enums.VisibleEnum;

import java.util.List;

public interface PropositionService {
    List<Proposition> findBenefits(Integer postId);
    List<Proposition> findPros(Integer postId);
    List<Proposition> findContras(Integer postId);
    List<Proposition> findOthers(Integer postId);

    List<Proposition> findByFeedbackAndVisible(Feedback feedback, VisibleEnum visible);

    void mask(Integer id);
    void unmask(Integer id);
    Proposition save(Proposition proposition);
    Proposition findById(Integer id);
}
