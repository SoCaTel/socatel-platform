package com.socatel.services.notification;

import com.socatel.models.Notification;
import com.socatel.models.User;
import com.socatel.repositories.NotificationRepository;
import com.socatel.services.email.EmailService;
import com.socatel.services.group.GroupService;
import com.socatel.services.user.UserService;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.GroupUserRelationEnum;
import com.socatel.utils.enums.NewEnum;
import com.socatel.utils.enums.OnOffEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;
    private UserService userService;
    private GroupService groupService;
    private final EmailService emailService;
    private final MessageSource messageSource;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserService userService,
                                   GroupService groupService,
                                   EmailService emailService,
                                   MessageSource messageSource) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.emailService = emailService;
        this.messageSource = messageSource;
    }

    @Override
    public Page<Notification> userNotifications(Integer userId, int page, int pageSize) {
        return notificationRepository.findByUserId(userId, PageRequest.of(page, pageSize, Constants.SORT_BY_TIMESTAMP_DESC));
    }

    @Override
    public Notification getNotificationById(Integer notificationId) {
        return notificationRepository.findById(notificationId).orElse(null);
    }

    @Override
    public Long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndNewNotif(user, NewEnum.NEW);
    }

    @Override
    public void readNotification(Notification notification) {
        if (notification.isNewNotification()) {
            notification.setNewNotif(NewEnum.NOT_NEW);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    @Override
    public void readAllNotifications(User user) {
        notificationRepository.updateAllNotificationsNew(user.getId());
    }

    @Override
    public void delete(Integer id, User user) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent() && notification.get().getUser().equals(user))
            notificationRepository.delete(notification.get());
    }

    @Override
    public void deleteAll(User user) {
        notificationRepository.deleteAllByUser(user);
    }

    @Async
    @Override
    public void notifyModerators(String message, String url, String reference) {
        userService.getModerators().forEach(moderator -> createNotification(message, moderator, url, reference));
    }

    @Async
    @Override
    public void notifyAllParticipantsInGroup(Integer groupId, String message, String url, String reference) {
        groupService.findUsersByGroupId(groupId).forEach(participant -> createNotification(message, participant, url, reference));
    }

    @Async
    @Override
    public void notifyCreatorInGroup(Integer groupId, String message, String url, String reference) {
        groupService.findUsersByGroupIdAndRelation(groupId, GroupUserRelationEnum.CREATED).forEach(creator -> createNotification(message, creator, url, reference));
    }

    @Async
    @Override
    public void notifySubscribersInGroup(Integer groupId, String message, String url, String reference) {
        groupService.findUsersByGroupIdAndRelation(groupId, GroupUserRelationEnum.SUBSCRIBED).forEach(subscriber -> createNotification(message, subscriber, url, reference));
    }

    @Async
    @Override
    public void notifyContributorsInGroup(Integer groupId, String message, String url, String reference) {
        groupService.findUsersByGroupIdAndRelation(groupId, GroupUserRelationEnum.CONTRIBUTOR).forEach(contributor -> createNotification(message, contributor, url, reference));
    }

    @Async
    @Override
    public void notifyUser(User user, String message, String url, String reference) {
        createNotification(message, user, url, reference);
    }

    private void createNotification(String body, User userTo, String url, String reference) {
        if (!userTo.isAnonymized()) { // Send notification to users not anonymous
            Notification notification = new Notification(body, userTo, url, reference);
            notificationRepository.save(notification);
            if (userTo.getNotifyByEmail().equals(OnOffEnum.ON)) // If notification to email is enabled
                sendNotificationToEmail(notification);
        }
    }

    private void sendNotificationToEmail(Notification notification) { // TODO change
        Locale locale = new Locale(notification.getUser().getFirstLang().getCode());
        String subject = messageSource.getMessage("email.new_notification.subject", null, locale);
        String link = Constants.PLATFORM_HOST + notification.getUrl();
        String body = messageSource.getMessage(notification.getText(), null, locale) +
                " " + notification.getReference();
        emailService.sendNotificationEmail(notification.getUser().getEmail(), subject, body, link, locale);
    }

}
