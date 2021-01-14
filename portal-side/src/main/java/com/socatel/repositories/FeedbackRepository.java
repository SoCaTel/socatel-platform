package com.socatel.repositories;

import com.socatel.models.Feedback;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository  extends JpaRepository<Feedback,Integer> {
    List<Feedback> findAllByPostIdAndVisible(Integer postId, VisibleEnum visible, Sort sort);
    List<Feedback> findAllByPostId(Integer postId, Sort sort);
}
