package com.socatel.services.chat;

import com.socatel.components.Methods;
import com.socatel.models.Chat;
import com.socatel.models.Message;
import com.socatel.models.User;
import com.socatel.repositories.ChatRepository;
import com.socatel.repositories.MessageRepository;
import com.socatel.services.email.EmailService;
import com.socatel.services.history.HistoryService;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.OnOffEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Service
public class ChatServiceImpl implements ChatService{

    private ChatRepository chatRepository;
    private MessageRepository messageRepository;
    private HistoryService historyService;
    private EmailService emailService;
    private MessageSource messageSource;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository, MessageRepository messageRepository,
                           HistoryService historyService, EmailService emailService,
                           MessageSource messageSource) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.historyService = historyService;
        this.emailService = emailService;
        this.messageSource = messageSource;
    }

    @Override
    public Chat findChatById(Integer id) {
        return chatRepository.findById(id).orElse(null);
    }

    @Override
    public Chat findChatBetween(User user1, User user2) {
        return chatRepository.findChatBetween(user1.getId(), user2.getId()).orElseGet(() -> createChat(user1, user2));
    }

    private Chat createChat(User user1, User user2) {
        return chatRepository.save(new Chat(user1, user2));
    }

    @Override
    public Page<Chat> findChats(Integer userId, int page, int pageSize) {
        return chatRepository.findChats(userId, PageRequest.of(page, pageSize));
    }

    @Override
    public Long countUnreadChats(Integer userId) {
        return chatRepository.countUnreadChats(userId);
    }

    @Override
    public Long countAllMessages() {
        return messageRepository.count();
    }

    @Override
    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    @Override
    public Message createMessage(Chat chat, User user, String text, HttpServletRequest request) {
        Message message = new Message(chat, user, text);
        chat.setLastMessage(message.getTimestamp());
        chat.updateLastSeen(user);
        chatRepository.save(chat);
        Message msg = messageRepository.save(message);
        User otherUser = chat.getOtherUser(user);
        historyService.createHistory(user, null, null, Methods.encrypt(otherUser.getId()), HistoryTypeEnum.MESSAGE_USER, VisibleEnum.HIDDEN);
        if (otherUser.getNotifyMessageByEmail().equals(OnOffEnum.ON)) {
            sendNotificationEmail(otherUser, user, request);
        }
        return msg;
    }

    private void sendNotificationEmail(User to, User from, HttpServletRequest request) {
        Locale locale = new Locale(to.getFirstLang().getCode());
        String subject = messageSource.getMessage("email.new_message.subject", null, locale);
        String link = request.getScheme() + "://" +
                request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath() + "/messages/" + from.getUsername();
        String body = messageSource.getMessage("email.new_message.body", new Object[]
                {from.getUsername().trim().replaceAll(" ", "%20")}, locale);
        emailService.sendNotificationEmail(to.getEmail(), subject, body, link, locale);
    }

    @Override
    public Message findMessageById(Integer id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Message> findAllMessages(Chat chat, int page, int pageSize) {
        return messageRepository.findAllByChat(chat, PageRequest.of(page, pageSize, Constants.SORT_BY_TIMESTAMP_ASC));
    }


}
