package com.socatel.repositories;

import com.socatel.models.Post;
import com.socatel.models.User;
import com.socatel.models.relationships.UserPostVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPostVoteRepository extends JpaRepository<UserPostVote, Integer> {
    Optional<UserPostVote> findByUserAndPost(User user, Post post);
}
