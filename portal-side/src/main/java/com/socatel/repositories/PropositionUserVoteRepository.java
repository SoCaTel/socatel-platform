package com.socatel.repositories;

import com.socatel.models.Proposition;
import com.socatel.models.User;
import com.socatel.models.relationships.PropositionUserVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropositionUserVoteRepository extends JpaRepository<PropositionUserVote,Integer> {
    Optional<PropositionUserVote> findByUserAndProposition(User user, Proposition proposition);
}
