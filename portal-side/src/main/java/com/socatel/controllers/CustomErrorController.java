package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.models.User;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

@Controller
public class CustomErrorController implements ErrorController {

    @Autowired private MessageSource messageSource;
    @Autowired private UserService userService;
    @Autowired private NotificationService notificationService;
    @Autowired private Methods methods;
    private Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * Handle errors
     * @param request request
     * @return error mav
     */
    @RequestMapping("/error")
    public ModelAndView error(HttpServletRequest request, Locale locale) {
        ModelAndView model = errorModel("errors/error", locale);
        logger.warn("Error page");
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            int statusCode = Integer.parseInt(status.toString());
            String message = "";
            if (throwable != null) {
                message = throwable.getMessage();
            }
            logger.error(statusCode + " " + message);
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addObject("error", "Error 404: Page not found\n" + message);
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addObject("error", "Error 500: Internal error\n" + message);
            } else {
                model.addObject("error", "Error " + statusCode + " " + message);
            }
        } else model.addObject("error", "Unexpected error");
        return model;
    }

    /**
     * Handle bad user error
     * @param message message to display
     * @param expired if the token is expired
     * @param token if there is a token
     * @return bad user mav
     */
    @RequestMapping(value = "/errors/badUser", method = RequestMethod.GET)
    public ModelAndView badUser(@ModelAttribute("message") String message,
                                @ModelAttribute("expired") Optional<String> expired,
                                @ModelAttribute("token") Optional<String> token,
                                Locale locale) {
        ModelAndView model = errorModel("errors/badUser", locale);
        logger.warn("Bad user");
        model.addObject("message", message);
        if (expired.isPresent() && !expired.get().isEmpty()) model.addObject("expired", true);
        if (token.isPresent() && !token.get().isEmpty()) model.addObject("token", token.get());
        return model;
    }

    /**
     * Create error model
     * @param locale locale
     * @return error mav
     */
    private ModelAndView errorModel(String errorPage, Locale locale) {
        ModelAndView model = new ModelAndView(errorPage);
        try {
            User user = Methods.getLoggedInUser(userService);
            model.addObject("lang_code", user.getFirstLang().getCode());
            model.addObject("newNotifNumber", notificationService.countUnreadNotifications(user));
        } catch (ClassCastException e) {
            model.addObject("lang_code", methods.getLanguageCodeFromLocale(locale));
        }
        return model;
    }

}
