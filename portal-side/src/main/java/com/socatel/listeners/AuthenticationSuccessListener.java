package com.socatel.listeners;

import com.socatel.models.User;
import com.socatel.services.history.HistoryService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.AnonEnum;
import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent interactiveAuthenticationSuccessEvent) {
        User user = (User) interactiveAuthenticationSuccessEvent.getAuthentication().getPrincipal();
        if (user.isAnonymized()) {
            user.setAnonymized(AnonEnum.NOT_ANONYMIZED);
            userService.save(user);
            historyService.createHistory(user, null, null, "history.restore_account", HistoryTypeEnum.RESTORE_ACCOUNT, VisibleEnum.VISIBLE);
        }
    }
}
