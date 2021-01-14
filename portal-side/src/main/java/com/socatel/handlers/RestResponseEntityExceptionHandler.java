package com.socatel.handlers;

import com.socatel.exceptions.FileStorageException;
import com.socatel.exceptions.GroupNotAccessibleException;
import com.socatel.exceptions.GroupNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({ UsernameNotFoundException.class })
    public ModelAndView handleUserNotFound(RuntimeException ex, Locale locale) {
        logger.error("404 Status Code", ex);
        String message = messageSource.getMessage("error.username_not_found", null, locale);
        return new ModelAndView("errors/error", "error", message + ": " + ex.getMessage());
    }

    @ExceptionHandler({ MailAuthenticationException.class })
    public ModelAndView handleMail(RuntimeException ex, Locale locale) {
        logger.error("500 Status Code", ex);
        String message = messageSource.getMessage("error.mail", null, locale);
        return new ModelAndView("errors/error", "error", message);
    }

    @ExceptionHandler({ FileStorageException.class })
    public ModelAndView handleFileStorage(RuntimeException ex) {
        logger.error("500 Status Code", ex);
        return new ModelAndView("errors/error", "error", ex.getMessage());
    }

    @ExceptionHandler({ GroupNotFoundException.class })
    public ModelAndView handleGroupNotFound(RuntimeException ex, Locale locale) {
        logger.error("404 Status Code", ex);
        String message = messageSource.getMessage("error.group_not_found", null, locale);
        return new ModelAndView("errors/error", "error", message + ": " + ex.getMessage());
    }

    @ExceptionHandler({ GroupNotAccessibleException.class })
    public ModelAndView handleGroupNotAccessible(RuntimeException ex, Locale locale) {
        logger.error("403 Status Code", ex);
        String message = messageSource.getMessage("error.group_not_accessible", null, locale);
        return new ModelAndView("errors/error", "error", message + ": " + ex.getMessage());
    }

    @ExceptionHandler({ Exception.class })
    public ModelAndView handleInternal(RuntimeException ex) {
        logger.error("500 Status Code", ex);
        return new ModelAndView("errors/error", "error", "InternalError: " + ex.getMessage());
    }
}