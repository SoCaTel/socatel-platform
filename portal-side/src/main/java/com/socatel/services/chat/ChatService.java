package com.socatel.services.chat;

import com.socatel.models.Chat;
import com.socatel.models.Message;
import com.socatel.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import javax.servlet.http.HttpServletRequest;

public interface ChatService {
    Chat findChatById(Integer id);
    Chat findChatBetween(User user1, User user2);
    Page<Chat> findChats(@Param("user") Integer userId, int page, int pageSize);
    Long countUnreadChats(@Param("user") Integer userId);
    Long countAllMessages();
    void save(Chat chat);
    Message createMessage(Chat chat, User user, String text, HttpServletRequest request);
    Message findMessageById(Integer id);
    Page<Message> findAllMessages(Chat chat, int page, int pageSize);
}
