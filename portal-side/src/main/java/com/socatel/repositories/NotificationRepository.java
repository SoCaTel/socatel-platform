package com.socatel.repositories;

import com.socatel.models.Notification;
import com.socatel.models.User;
import com.socatel.utils.enums.NewEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {

    //@Query("select n from Notification n where n.user.id=:userId ORDER BY n.timestamp DESC")
    Page<Notification> findByUserId(Integer userId, Pageable pageable);
    Long countByUserAndNewNotif(User user, NewEnum newEnum);
    @Modifying
    @Query("UPDATE Notification n SET n.newNotif = 1 WHERE n.user.id = :user_id")
    void updateAllNotificationsNew(Integer user_id);
    void deleteAllByUser(User user);
}
