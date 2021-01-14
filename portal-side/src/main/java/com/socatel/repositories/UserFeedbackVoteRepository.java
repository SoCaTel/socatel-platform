package com.socatel.repositories;

import com.socatel.models.Feedback;
import com.socatel.models.User;
import com.socatel.models.relationships.UserFeedbackVote;
import com.socatel.utils.enums.VoteTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFeedbackVoteRepository extends JpaRepository<UserFeedbackVote, Integer> {
    Optional<UserFeedbackVote> findByUserAndFeedback(User user, Feedback feedback);
    Integer countByFeedbackAndVoteType(Feedback feedback, VoteTypeEnum voteType);
}
