package com.socatel.services.notification;

import com.socatel.models.Notification;
import com.socatel.models.User;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;

public interface NotificationService {
    Page<Notification> userNotifications(Integer userId, int page, int pageSize);
    void readNotification(Notification notification);
    void readAllNotifications(User user);
    Notification getNotificationById(Integer notificationId);
    Long countUnreadNotifications(User user);
    void delete(Integer id, User user);
    void deleteAll(User user);
    @Async void notifyModerators(String message, String url, String reference);
    @Async void notifyAllParticipantsInGroup(Integer groupId, String message, String url, String reference);
    @Async void notifyCreatorInGroup(Integer groupId, String message, String url, String reference);
    @Async void notifySubscribersInGroup(Integer groupId, String message, String url, String reference);
    @Async void notifyContributorsInGroup(Integer groupId, String message, String url, String reference);
    @Async void notifyUser(User user, String message, String url, String reference);
}
