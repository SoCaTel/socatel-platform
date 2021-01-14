package com.socatel.services.email;

import org.springframework.mail.SimpleMailMessage;

import java.util.Locale;

public interface EmailService {
    void sendEmail(String to, String subject, String text, Locale locale);
    void sendNotificationEmail(String to, String subject, String text, String url, Locale locale);
    void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String ...templateArgs);
    void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);
}