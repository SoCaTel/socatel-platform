package com.socatel.listeners;

import com.socatel.events.OnRegistrationCompleteEvent;
import com.socatel.models.User;
import com.socatel.services.email.EmailService;
import com.socatel.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = messageSource.getMessage("email.regSucc.subject", null, event.getLocale());
        String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        String message = messageSource.getMessage("email.regSucc.body", null, event.getLocale());
        emailService.sendEmail(recipientAddress, subject, message + "\n" + confirmationUrl, event.getLocale());
    }
}
