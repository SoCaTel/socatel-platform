package com.socatel.repositories;

import com.socatel.models.Feedback;
import com.socatel.models.Proposition;
import com.socatel.utils.enums.PropositionTypeEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropositionRepository extends JpaRepository<Proposition,Integer> {
    List<Proposition> findByPostIdAndTypeAndVisible(int post_id, PropositionTypeEnum type, VisibleEnum visible, Sort sort);
    List<Proposition> findByFeedbackAndVisible(Feedback feedback, VisibleEnum visible, Sort sort);
}
