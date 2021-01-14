package com.socatel.components;

import com.socatel.models.Group;
import com.socatel.models.User;
import com.socatel.repositories.LanguageRepository;
import com.socatel.services.group.GroupService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.user.UserService;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.GroupStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class Methods {

    private final MessageSource messageSource;
    private final GroupService groupService;
    private final PostService postService;
    private final NotificationService notificationService;
    private final LanguageRepository languageRepository;

    private Logger logger = LoggerFactory.getLogger(Methods.class);

    @Autowired
    public Methods(MessageSource messageSource, GroupService groupService, PostService postService, NotificationService notificationService, LanguageRepository languageRepository) {
        this.messageSource = messageSource;
        this.groupService = groupService;
        this.postService = postService;
        this.notificationService = notificationService;
        this.languageRepository = languageRepository;
    }

    /**
     * Calculate the time remaining of a group until next step
     * @param group group
     * @param locale locale
     * @return time left
     */
    public String timeRemaining(Group group, Locale locale) {
        if (group.getStatus().equals(GroupStatusEnum.COMPLETED)) // TODO if today, replace 'in' by 'at'
            return messageSource.getMessage("find_topic.finished_in", null, locale) + " " + group.fancyNextStepTimestamp();
        if (group.getStatus().equals(GroupStatusEnum.TEST)) // TODO if today, replace 'in' by 'at'
            return messageSource.getMessage("find_topic.started_test", null, locale) + " " + group.fancyNextStepTimestamp();
        if (group.getStatus().equals(GroupStatusEnum.CODESIGN)) // TODO if today, replace 'in' by 'at'
            return messageSource.getMessage("find_topic.started_in", null, locale) + " " + group.fancyNextStepTimestamp();
        Long milis = group.milisRemaining();
        if (milis == Long.MAX_VALUE) return messageSource.getMessage("find_topic.not_started", null, locale);
        int days = (int) TimeUnit.MILLISECONDS.toDays(milis);
        int hours = (int) TimeUnit.MILLISECONDS.toHours(milis);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(milis) % 60);
        if (days > 0) return days + " " + messageSource.getMessage("find_topic.days_left", null, locale);
        if (days < 0 || hours < 0 || minutes < 0) {
            nextStep(group.getId());
            return timeRemaining(group, locale);
        }
        return messageSource.getMessage("find_topic.time_left", null, locale) + " " + hours + "h " + minutes + "m";
    }

    /**
     * Forward group to next step or extend step time
     * @param id group id
     */
    public void nextStep(Integer id) {
        logger.debug("Next step group id " + id);
        Group group = groupService.findById(id);
        if (group != null && hasToMoveStep(group)) {
            switch (group.getStatus()) {
                case IDEATION:
                    group.setNextStepTimestamp(Methods.oneMonthFromNow());
                    if (postService.findAllGroupIdeas(id).size() > 0) { // If there are ideas
                        group.setStatus(GroupStatusEnum.VALIDATION);
                        notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_advanced_automatically", "/topic/" + id, group.getName());
                    } // Added extra month if no ideas proposed.
                    else notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_time_extended", "/topic/" + id, group.getName());
                    groupService.save(group);
                    break;
                case VALIDATION:
                    group.setNextStepTimestamp(new Timestamp(System.currentTimeMillis()));
                    group.setStatus(GroupStatusEnum.CODESIGN);
                    groupService.save(group);
                    notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_advanced_automatically", "/topic/" + id, group.getName());
                    break;
            }
        }
    }

    /**
     * Check whether group has to move to the next step
     * @param group group
     * @return has to move or not
     */
    private boolean hasToMoveStep(Group group) {
        Long milis = group.milisRemaining();
        if (milis == Long.MAX_VALUE) return false;
        int days = (int) TimeUnit.MILLISECONDS.toDays(milis);
        int hours = (int) TimeUnit.MILLISECONDS.toHours(milis);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(milis) % 60);
        return (group.getStatus().equals(GroupStatusEnum.IDEATION) || group.getStatus().equals(GroupStatusEnum.VALIDATION)) &&
                (days < 0 || hours < 0 || minutes < 0);
    }

    /**
     * Get logged in user
     * @param userService userService
     * @return logged in user
     */
    public static User getLoggedInUser(UserService userService) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findById(user.getId());
    }

    /**
     * Returns language code if the platform has its translations, otherwise return 'en' as it is the default language
     * @param userService userService to look for the user language
     * @param locale locale where to get the language code in case it is an anonymous user
     * @return language code
     */
    public String getLanguageCode(UserService userService, Locale locale) {
        String code;
        try {
            User user = getLoggedInUser(userService);
            code = user.getFirstLang().getCode();
        } catch (ClassCastException e) {
            code = getLanguageCodeFromLocale(locale);
        }
        return code;
    }

    /**
     * Returns language code if the platform has its translations, otherwise return 'en' as it is the default language
     * @param locale locale where to get the language code
     * @return locale's language code or 'en'
     */
    public String getLanguageCodeFromLocale(Locale locale) {
        List<String> codes = (List<String>) languageRepository.findAllCodes();
        if (codes.contains(locale.getLanguage()))
            return locale.getLanguage();
        else return "en";
    }

    /**
     * Display date in a proper format
     * @param date date
     * @return formatted date
     */
    public static String formatDate(Date date) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");

        // set the calendar to start of day
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();

        // -1 day
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        // Today
        if (date.after(today) && date.before(tomorrow)) return sdf.format(date);

        // Not today
        sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        return sdf.format(date);
    }

    /**
     * Difference in milliseconds between a date and now
     * @param date date
     * @return milliseconds
     */
    public static Long milisRemaining(Date date) {
        Date now = new Date();
        if (date == null) return Long.MAX_VALUE;
        return date.getTime() - now.getTime();
    }

    /**
     * Count one month from now
     * @return now+1M
     */
    public static Timestamp oneMonthFromNow() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, 1);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Check if the user's input is valid
     * @param s user input
     * @return valid
     */
    public static boolean isValidUserInput(String s) {
        //TODO update
        return s.matches("[A-Za-z0-9]+");
    }

    /**
     * Add noise to a userId and then encrypts it
     * @param userId userId
     * @return encryptedId
     */
    public static String encrypt(Integer userId) {
        String noisyId = addNoise(userId);
        return Constants.te.encrypt(noisyId);
    }

    /**
     * Decrypts an encryptedId and then removes its noise
     * @param encryptedId encryptedId
     * @return userId
     */
    public static Integer decrypt(String encryptedId) {
        String noisyId = Constants.te.decrypt(encryptedId);
        return removeNoise(noisyId);
    }

    /**
     * Add noise to a userId adding 5 random digits in front and behind the id
     * @param userId userId
     * @return id with noise
     */
    private static String addNoise(Integer userId) {
        return Constants.NOISE_BEFORE + userId + Constants.NOISE_AFTER;
    }

    /**
     * Remove noise to a userId removing 5 digits in front and behind the id
     * @param noisyId id with noise
     * @return userId
     */
    private static Integer removeNoise(String noisyId) {
        // Remove 5 first numbers of the id
        noisyId = noisyId.substring(5);
        // Remove 5 last numbers of the id
        noisyId = noisyId.substring(0, noisyId.length() - 5);
        return Integer.parseInt(noisyId);
    }

    /*
    public static <T> Page<T> toDTO(Page<T> page) {
        return new PageImpl<>(toDTO(page.getContent()), page.getPageable(), page.getTotalElements());
    }

    private static <T> List<T> toDTO(List<T> list) {
        return list.stream().map(T::new).collect(Collectors.toList());
    }
    */
}
