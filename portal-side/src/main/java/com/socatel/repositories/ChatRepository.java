package com.socatel.repositories;

import com.socatel.models.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    @Query("select c from Chat c where (c.user1.id = :user1 and c.user2.id = :user2) " +
            "or (c.user2.id = :user1 and c.user1.id = :user2) ")
    Optional<Chat> findChatBetween(@Param("user1") Integer userId, @Param("user2")Integer userId2);

    @Query("select c from Chat c where (c.user1.id = :user or c.user2.id = :user) " +
            "and (c.user1.anonymized = 0 and c.user2.anonymized = 0) " +
            "and c.lastMessage IS NOT NULL " +
            "order by c.lastMessage desc")
    Page<Chat> findChats(@Param("user") Integer userId, Pageable pageable);

    @Query("select count(c) from Chat c where " +
            "((c.user1.id = :user and c.lastSeen1 < c.lastMessage and c.user2.anonymized = 0) or " +
            "(c.user2.id = :user and c.lastSeen2 < c.lastMessage and c.user1.anonymized = 0))")
    Long countUnreadChats(@Param("user") Integer userId);
}
