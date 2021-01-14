package com.socatel.repositories;

import com.socatel.models.Chat;
import com.socatel.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    Page<Message> findAllByChat(Chat chat, Pageable pageable);
}
