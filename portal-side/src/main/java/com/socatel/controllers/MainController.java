package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.dtos.ContactMessageDTO;
import com.socatel.models.Group;
import com.socatel.models.User;
import com.socatel.services.chat.ChatService;
import com.socatel.services.email.EmailService;
import com.socatel.services.group.GroupService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController {

    @Autowired private EmailService emailService;
    @Autowired private MessageSource messageSource;
    @Autowired private GroupService groupService;
    @Autowired private UserService userService;
    @Autowired private PostService postService;
    @Autowired private ChatService chatService;
    @Autowired private NotificationService notificationService;
    @Autowired private Methods methods;

    private Logger logger = LoggerFactory.getLogger(MainController.class);

    /**
     * Display main page
     * @param locale locale
     * @return index mav
     */
    @RequestMapping(value = {"/", "/index", "/index.html"}, method = RequestMethod.GET)
    public ModelAndView index(Locale locale) {
        logger.debug("Index");
        ModelAndView model = new ModelAndView("index", "contactMessage", new ContactMessageDTO());
        List<Group> popularTopics;
        Resource resource;
        String imageName = "intro-bg_";
        // TODO like this?
        try {
            User user = Methods.getLoggedInUser(userService);
            popularTopics = groupService.findRecentGroupsLoggedIn(
                    user.getLocality().getId(),
                    user.getParentLocality().getId(),
                    user.getFirstLang().getCode(),
                    user.getSecondLang() != null ? user.getSecondLang().getCode() : null
            );
            imageName = imageName + user.getFirstLang().getCode() + ".jpg";
            resource = new ClassPathResource("static/images/" + imageName);
            model.addObject("newNotifNumber", notificationService.countUnreadNotifications(user));
            model.addObject("lang_code", user.getFirstLang().getCode());
        } catch (ClassCastException ex) {
            popularTopics = groupService.findRecentGroupsAnonymously(locale.getLanguage());
            imageName = imageName + locale.getLanguage() + ".jpg";
            resource = new ClassPathResource("static/images/" + imageName);
            model.addObject("lang_code", methods.getLanguageCodeFromLocale(locale));
        }
        // TODO or like this?
        //popularTopics = groupService.findRecentGroups();
        List<String> timesRemaining = new LinkedList<>();
        List<Long> participants = new LinkedList<>();
        List<Long> subscribers = new LinkedList<>();
        for (Group group : popularTopics) {
            timesRemaining.add(methods.timeRemaining(group, locale));
            participants.add(groupService.countContributors(group.getId()));
            subscribers.add(groupService.countSubscribers(group.getId()));
        }
        model.addObject("recent_groups", popularTopics);
        model.addObject("times_remaining", timesRemaining);
        model.addObject("participants", participants);
        model.addObject("subscribers", subscribers);
        model.addObject("num_topics", groupService.countAll());
        model.addObject("num_users", userService.countAll());
        model.addObject("num_messages", chatService.countAllMessages());
        model.addObject("num_ideas", postService.countAllIdeas());
        if (resource.exists())
            model.addObject("image_name", imageName);
        else model.addObject("image_name", "intro-bg_en.jpg");
        return model;
    }

    /**
     * Display FAQ
     * @return FAQ mav
     */
    @RequestMapping(value = {"/faq", "/faq.html"}, method = RequestMethod.GET)
    public String faq(Locale locale) {
        ModelAndView model = new ModelAndView("faq");
        try {
            User user = Methods.getLoggedInUser(userService);
            model.addObject("newNotifNumber", notificationService.countUnreadNotifications(user));
            model.addObject("lang_code", user.getFirstLang().getCode());
        } catch (ClassCastException ignored) {
            model.addObject("lang_code", methods.getLanguageCodeFromLocale(locale));
        }
        return "redirect:/";
    }

    /**
     * Display privacy policy
     * @param locale locale
     * @return privacy policy mav
     */
    @RequestMapping(value = {"/privacy-policy", "/privacy-policy.html"}, method = RequestMethod.GET)
    public ModelAndView privacyPolicy(Locale locale) {
        ModelAndView model = genericModel(locale);
        model.addObject("page", "privacy");
        return model;
    }

    /**
     * Display terms and conditions
     * @param locale locale
     * @return terms and conditions mav
     */
    @RequestMapping(value = {"/terms-conditions", "/terms-conditions.html"}, method = RequestMethod.GET)
    public ModelAndView termsConditions(Locale locale) {
        ModelAndView model = genericModel(locale);
        model.addObject("page", "terms_conditions");
        return model;
    }

    /**
     * Display cookie policy
     * @param locale locale
     * @return cookies mav
     */
    @RequestMapping(value = {"/cookie-policy", "/cookie-policy.html"}, method = RequestMethod.GET)
    public ModelAndView cookiePolicy(Locale locale) {
        ModelAndView model = genericModel(locale);
        model.addObject("page", "cookies");
        return model;
    }

    /**
     * Create a generic model
     * @param locale locale
     * @return generic mav
     */
    private ModelAndView genericModel(Locale locale) {
        ModelAndView model = new ModelAndView("generic");
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
