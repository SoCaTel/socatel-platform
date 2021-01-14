package com.socatel.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Locale;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired private MessageSource messageSource;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String from;

    private void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(from);

            javaMailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void sendEmail(String to, String subject, String text, Locale locale) {
        sendSimpleMessage(to, subject, formatMessage(text, locale));
    }

    @Override
    public void sendNotificationEmail(String to, String subject, String text, String url, Locale locale) {
        sendSimpleMessage(to, subject, formatMessage(text, url, locale));
    }

    private String formatMessage(String text, Locale locale) {
        String dear = messageSource.getMessage("email.dear", null, locale);
        String regards = messageSource.getMessage("email.regards", null, locale);
        String socatel = messageSource.getMessage("email.socatel_team", null, locale);
        return dear + ",\n\n" + // TODO add username?
                        text + "\n\n" +
                        regards + ",\n" + socatel;
    }

    private String formatMessage(String text, String url, Locale locale) {
        String click_link = messageSource.getMessage("email.click_link", null, locale);
        return formatMessage(
                text + "\n" +
                click_link + ": " + url, locale);
    }

    @Override
    public void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template,
                                               String... templateArgs) {
        String text = String.format(template.getText(), templateArgs);
        sendSimpleMessage(to, subject, text);
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom(from);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
