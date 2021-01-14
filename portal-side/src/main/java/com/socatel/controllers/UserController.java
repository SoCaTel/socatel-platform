package com.socatel.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.socatel.components.Methods;
import com.socatel.components.PDFGenerator;
import com.socatel.dtos.EmailDTO;
import com.socatel.dtos.MessageWithDocuments;
import com.socatel.dtos.PasswordDTO;
import com.socatel.dtos.UserData;
import com.socatel.events.OnRegistrationCompleteEvent;
import com.socatel.exceptions.ImageException;
import com.socatel.exceptions.KnowledgeBaseException;
import com.socatel.exceptions.NotServiceProviderException;
import com.socatel.models.*;
import com.socatel.models.tokens.PasswordResetToken;
import com.socatel.models.tokens.VerificationToken;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.*;
import com.socatel.rest_api.RestAPICaller;
import com.socatel.rest_api.rdi.ServiceByKeywords;
import com.socatel.services.chat.ChatService;
import com.socatel.services.document.DocumentService;
import com.socatel.services.email.EmailService;
import com.socatel.services.group.GroupService;
import com.socatel.services.history.HistoryService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.organisation.OrganisationService;
import com.socatel.services.post.PostService;
import com.socatel.services.report.ReportService;
import com.socatel.services.service.ServiceService;
import com.socatel.services.user.UserService;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class UserController {

    @Autowired private UserService userService;
    @Autowired private GroupService groupService;
    @Autowired private ChatService chatService;
    @Autowired private MessageSource messageSource;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private NotificationService notificationService;
    @Autowired private HistoryService historyService;
    @Autowired private EmailService emailService;
    @Autowired private LocalityRepository localityRepository;
    @Autowired private LanguageRepository languageRepository;
    @Autowired private ThemeRepository themeRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private ServiceService serviceService;
    @Autowired private ReportService reportService;
    @Autowired private ReportRepository reportRepository;
    @Autowired private DocumentService documentService;
    @Autowired private ElasticsearchPublisher elasticsearchPublisher;
    @Autowired private OrganisationService organisationService;
    @Autowired private PDFGenerator pdfGenerator;
    @Autowired private RestAPICaller restAPICaller;
    @Autowired private Methods methods;
    @Autowired private PostService postService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * User sign up
     * @param user new user
     * @param bindingResult binding result
     * @param request servlet request
     * @return regSucc mav
     */
    @RequestMapping(value = {"/registration"}, method = RequestMethod.POST)
    public ModelAndView registerUser(@Valid User user, BindingResult bindingResult, HttpServletRequest request,
                                     @RequestParam("token") Optional<String> token) {
        logger.debug("Register User");
        ModelAndView model = new ModelAndView();
        User userExists = userService.findByUsername(user.getUsername());
        User emailExists = userService.findByEmail(user.getEmail());

        if (userExists != null)
            bindingResult.rejectValue("username", "error.user", messageSource.getMessage(
                    "user_username.exists", null, request.getLocale()));

        if  (emailExists != null)
            bindingResult.rejectValue("email", "error.user", messageSource.getMessage(
                    "user_email.exists", null, request.getLocale()));

        if (!user.passwordsMatch())
            bindingResult.rejectValue("matchingPassword", "error.user", messageSource.getMessage(
                    "user_password.match", null, request.getLocale()));

        if (user.getLocality() == null)
            bindingResult.rejectValue("locality", "error.user", messageSource.getMessage(
                    "user_locality.value", null, request.getLocale()));

        if (user.getFirstLang() == null)
            bindingResult.rejectValue("firstLang", "error.user", messageSource.getMessage(
                    "user_language.value", null, request.getLocale()));

        if (user.getProfile() == null)
            bindingResult.rejectValue("profile", "error.user", messageSource.getMessage(
                    "user_profile.value", null, request.getLocale()));

        if (user.getAgeRange() == null)
            bindingResult.rejectValue("ageRange", "error.user", messageSource.getMessage(
                    "user_age_range.value", null, request.getLocale()));

        if (bindingResult.hasErrors()) {
            model.addObject("localities", localityRepository.findAllByLocalityParentNotNull());
            model.addObject("languages", languageRepository.findAll());
            model.setViewName("registration");
        } else {
            createUser(user, request.getLocale(), token);
            String appUrl = request.getRequestURL().toString().replace("/registration", "");
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
            model.setViewName("redirect:/regSucc");
            logger.debug("Created New User");
        }

        return model;
    }

    /**
     * Create new user and store in DB
     * @param user user data from registration
     */
    private void createUser(User user, Locale locale, Optional<String> token) {
        Invitation invitation = null;
        user.setParentLocality(user.getLocality().getLocalityParent());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setMatchingPassword(null);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        user.setPicture(randomDefaultPicture(user.getId(), locale));
        if (token.isPresent() && !token.get().isEmpty()) {
            invitation = organisationService.findInvitationByToken(token.get());
            if (invitation != null && invitation.getStatus().compareTo(InvitationStatus.SENT) == 0) {
                user.setOrganisation(invitation.getOrganisation());
                user.setOrgRole(invitation.getRole());
                user.setProfile(ProfileEnum.SERVICE_PROVIDER);
                organisationService.updateInvitationStatus(invitation, InvitationStatus.ACCEPTED);
            }
        }
        userService.save(user);
        if (invitation != null) {
            historyService.createHistory(user, invitation.getOrganisation(), null, "history.joined_organisation", HistoryTypeEnum.JOINED_ORGANISATION, VisibleEnum.VISIBLE);
            logger.debug("Accepted invitation " + user.getUsername());
        }
    }

    /**
     * Get a random avatar
     * @param userId userId
     * @param locale locale
     * @return avatar
     */
    private Document randomDefaultPicture(Integer userId, Locale locale) {
        return documentService.getFile((userId % Constants.DEFAULT_PICTURES_NUMBER) + 1, locale);
    }

    /**
     * Return to previous page (http referer)
     * @param request servlet request
     * @return redirect to dashboard
     */
    private String returnToPreviousPage(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (!referer.isEmpty()) {
            return "redirect:" + referer.replace(request.getRequestURL().toString().replace(request.getRequestURI(), ""), "");
        }
        return "redirect:/dashboard";
    }

    /**
     * Change avatar
     * @param number avatar number
     * @param locale locale
     * @param request servlet request
     * @return return to previous page
     */
    @GetMapping("/change-avatar/{number}")
    public String changeAvatar(@PathVariable("number") Integer number, Locale locale, HttpServletRequest request) {
        User user = Methods.getLoggedInUser(userService);
        Document document = documentService.getFile(number, locale);
        userService.setProfilePicture(user, document);
        return returnToPreviousPage(request);
    }

    /**
     * Upload picture as avatar
     * @param file picture
     * @param locale locale
     * @param request servlet request
     * @return return to previous page
     */
    @PostMapping("/uploadPicture")
    public String uploadPicture(@RequestParam("file") MultipartFile file, Locale locale, HttpServletRequest request) {
        User user = Methods.getLoggedInUser(userService);
        if (file == null || file.getContentType() == null || !file.getContentType().contains("image"))
            throw new ImageException(messageSource.getMessage("error.image_format", null, locale));
        Document document = documentService.storeFile(file, null, null, null, null, null, null, locale);
        userService.setProfilePicture(user, document);
        return returnToPreviousPage(request);
    }

    /**
     * Display registration success
     * @return regSucc mav
     */
    @RequestMapping(value = {"/regSucc", "/regSucc.html"}, method = RequestMethod.GET)
    public ModelAndView registrationSuccess(Locale locale) {
        ModelAndView model = new ModelAndView("regSucc");
        model.addObject("lang_code", methods.getLanguageCode(userService, locale));
        return model;
    }

    /**
     * Confirmation of registration
     * @param request web request
     * @param token token
     * @param redirectAttributes redirect attributes
     * @return redirect to login
     */
    @RequestMapping(value = {"/registrationConfirm", "/registrationConfirm.html"}, method = RequestMethod.GET)
    public String confirmRegistration(WebRequest request, @RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            redirectAttributes.addFlashAttribute(
                    "message", messageSource.getMessage("auth.message.invalidToken", null, locale));
            return "redirect:/errors/badUser";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("auth.message.expired", null, locale));
            redirectAttributes.addFlashAttribute("expired", "true");
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/errors/badUser";
        }

        user.setEnabled(EnableEnum.ENABLED);
        userService.save(user);
        logger.debug("Registration confirmed user " + user.getId() + " " + user.getUsername());
        return "redirect:/login";
    }

    /**
     * Resend registration token
     * @param request servlet request
     * @param existingToken existing token
     * @return redirect to regSucc
     */
    @RequestMapping(value = "/resendRegistrationToken")
    public String resendRegistrationToken(HttpServletRequest request,
                                          @RequestParam("token") String existingToken) {
        VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        if (newToken == null) {
            return "redirect:/registration-token-email";
        }
        String token = newToken.getToken();
        User user = userService.getUser(token);
        String appUrl = request.getScheme() + "://" +
                        request.getServerName() +
                        ":" + request.getServerPort() +
                        request.getContextPath();
        String subject = messageSource.getMessage("email.resendToken.subject", null, request.getLocale());
        String message = messageSource.getMessage("email.resendToken.body", null, request.getLocale());
        emailService.sendEmail(user.getEmail(), subject, message + "\n" + appUrl + "/registrationConfirm?token=" + token, request.getLocale());
        logger.debug("Resent registration token to " + user.getUsername());
        return "redirect:/regSucc";
    }

    /**
     * Display registration token email
     * @return registration-token-email mav
     */
    @RequestMapping(value = {"/registration-token-email", "/registration-token-email.html"}, method = RequestMethod.GET)
    public ModelAndView registrationTokenEmail(Locale locale) {
        ModelAndView model = new ModelAndView("registration-token-email", "email", new EmailDTO());;
        model.addObject("lang_code", methods.getLanguageCode(userService, locale));
        return model;
    }

    /**
     * User might have another mail with the token.
     * Regardless, they can type their email and ask for another one.
     * If the email doesn't exist register.
     * If exists, eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
     */
    @RequestMapping(value = {"/registration-token-email", "/registration-token-email.html"}, method = RequestMethod.POST)
    public String registrationTokenEmail(@Valid EmailDTO email, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) return "redirect:/registration-token-email?error=0";
        User user = userService.findByEmail(email.getValue());
        if (user == null) return "redirect:/registration-token-email?error=1";
        String appUrl = request.getScheme() + "://" +
                        request.getServerName() +
                        ":" + request.getServerPort() +
                        request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        return "redirect:/regSucc";
    }

    /**
     * Display sign up page
     * @return registration mav
     */
    @RequestMapping(value = {"/registration", "/registration.html"})
    public ModelAndView registration(Locale locale) {
        logger.debug("Registration page");
        ModelAndView model = new ModelAndView("registration", "user", new User());
        model.addObject("lang_code", methods.getLanguageCode(userService, locale));
        model.addObject("localities", localityRepository.findAllByLocalityParentNotNull());
        model.addObject("languages", languageRepository.findAll());
        return model;
    }

    /**
     * Display login page
     * @return login mav
     */
    @RequestMapping(value = {"/login", "/login.html"}, method = RequestMethod.GET)
    public ModelAndView showLogin(HttpServletRequest request, Locale locale) {
        logger.debug("Login page");
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", referrer);
        ModelAndView model = new ModelAndView("login", "user", new User());
        model.addObject("lang_code", methods.getLanguageCode(userService, locale));
        return model;
    }

    /* /login POST is handled by Spring Security: UserDetails */

    /**
     * Display forgot password
     * @return forgot-password mav
     */
    @RequestMapping(value = {"/forgot-password", "/forgot-password.html"}, method = RequestMethod.GET)
    public ModelAndView forgotPassword(Locale locale) {
        ModelAndView model = new ModelAndView("forgot-password", "email", new EmailDTO());
        model.addObject("lang_code", methods.getLanguageCode(userService, locale));
        return model;
    }

    /**
     * Display reset password
     * @param email email to send the token
     * @param bindingResult binding result
     * @param request servlet request
     * @return redirect to regSucc
     */
    @RequestMapping(value = {"/reset-password"}, method = RequestMethod.POST)
    public String resetPassword(@Valid EmailDTO email, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) return "redirect:/forgot-password?error=0";
        User user = userService.findByEmail(email.getValue());
        if (user == null) return "redirect:/forgot-password?error=1";
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user, token);
        String appUrl = request.getScheme() + "://" +
                request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
        String subject = messageSource.getMessage("email.resetPassword.subject", null, request.getLocale());
        String message = messageSource.getMessage("email.resetPassword.body", null, request.getLocale());
        emailService.sendEmail(user.getEmail(), subject, message + "\n" + appUrl + "/reset-password?id=" + user.getId() + "&token=" + token, request.getLocale());
        logger.debug("Reset password token sent to " + user.getUsername());
        return "redirect:/regSucc";
    }

    /**
     * Reset password
     * @param userId user id
     * @param token token
     * @return redirect update password
     */
    @RequestMapping(value = {"/reset-password"}, method = RequestMethod.GET)
    public String resetPassword(@RequestParam(value = "id") Integer userId, @RequestParam(value = "token") String token) {
        PasswordResetToken pwdToken = userService.getPasswordResetToken(token, userId);
        if (pwdToken == null || pwdToken.getUser().getId() != userId) return "redirect:/forgot-password?invalid";
        Calendar cal = Calendar.getInstance();
        if ((pwdToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) return "redirect:/forgot-password?expired";
        User user = pwdToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, Arrays.asList(
                new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        logger.debug("Reset token received from " + user.getUsername());
        return "redirect:/update-password";
    }

    /**
     * Display update password
     * @return update-password mav
     */
    @PreAuthorize("hasAuthority('CHANGE_PASSWORD_PRIVILEGE')")
    @RequestMapping(value = {"/update-password"}, method = RequestMethod.GET)
    public ModelAndView updatePassword(Locale locale) {
        ModelAndView model = new ModelAndView("update-password", "passwordReset", new PasswordDTO());;
        model.addObject("lang_code", methods.getLanguageCode(userService, locale));
        return model;
    }

    /**
     * Update password
     * @param passwordDTO new password
     * @param bindingResult binding result
     * @param request servlet request
     * @return redirect to login
     */
    @PreAuthorize("hasAuthority('CHANGE_PASSWORD_PRIVILEGE')")
    @RequestMapping(value = {"/update-password"}, method = RequestMethod.POST)
    public String updatePassword(@Valid PasswordDTO passwordDTO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors())
            return "redirect:/update-password";
        if (!passwordDTO.passwordsMatches())
            return "redirect:/update-password";
        User user = Methods.getLoggedInUser(userService);
        user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
        userService.save(user);
        logger.debug("Password changed from user " + user.getUsername());
        return "redirect:/login";
    }

    /**
     * Display user dashboard
     * @param topicSize topics size
     * @param messageSize messages size
     * @param historySize hisotory size
     * @param notificationSize notification size
     * @return dashboard mav
     */
    @RequestMapping(value = {"/dashboard", "/dashboard.html"}, method = RequestMethod.GET)
    public ModelAndView showUserDashboard(@RequestParam("topic_size") Optional<Integer> topicSize,
                                          @RequestParam("message_size") Optional<Integer> messageSize,
                                          @RequestParam("history_size") Optional<Integer> historySize,
                                          @RequestParam("notification_size") Optional<Integer> notificationSize) {
        logger.debug("User dashboard page");
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = createUserMV(user, "dashboard");
        model.addObject("groupUserRelationsPage", groupService.findGroupRelationsByUser(user, 0 , topicSize.orElse(Constants.DASHBOARD_TOPIC_PAGE_SIZE)));
        model.addObject("chatsPage", chatService.findChats(user.getId(), 0, messageSize.orElse(Constants.DASHBOARD_MESSAGE_PAGE_SIZE)));
        model.addObject("historyPage", historyService.getUserHistory(user, 0, historySize.orElse(Constants.DASHBOARD_HISTORY_PAGE_SIZE)));
        model.addObject("notificationsPage", notificationService.userNotifications(user.getId(), 0, notificationSize.orElse(Constants.DASHBOARD_NOTIFICATION_PAGE_SIZE)));
        addKnowledgeBaseUserRecommendations(model, user);
        return model;
    }

    /**
     * Display user profile
     * @return profile mav
     */
    @RequestMapping(value = {"/my-profile", "/my-profile.html"}, method = RequestMethod.GET)
    public ModelAndView showUserProfile() {
        logger.debug("User profile page");
        User user = Methods.getLoggedInUser(userService);
        user = userService.findByUsername(user.getUsername());
        ModelAndView model = createUserMV(user, "my-profile");
        model.addObject("newUser", user);
        model.addObject("passwordDTO", new PasswordDTO());
        model.addObject("localities", localityRepository.findAllByLocalityParentNotNull());
        model.addObject("languages", languageRepository.findAll());
        model.addObject("skills", skillRepository.findAll());
        model.addObject("themes", themeRepository.findAll());
        return model;
    }

    /**
     * Attach knowledge base recommendations to the model
     * @param model model
     * @param user user
     */
    private void addKnowledgeBaseUserRecommendations(ModelAndView model, User user) {
        if (user.getSkills() != null && !user.getSkills().isEmpty())
            model.addObject("groups_skills", restAPICaller.getPopularGroupsBySkills(user.getSkills(), user.getLocality().getId()));
        if (user.getThemes() != null && !user.getThemes().isEmpty())
            model.addObject("groups_themes", restAPICaller.getPopularGroupsByThemes(user.getThemes(), user.getLocality().getId()));
        model.addObject("groups_user", restAPICaller.getGroupsGivenUser(user.getId(), user.getLocality().getId()));
        model.addObject("services_user", restAPICaller.getServicesGivenUser(user.getId()));
    }

    /**
     * Update profile password
     * @param passwordDTO new password
     * @param attributes redirect attributes
     * @return redirect to user profile
     */
    @Transactional
    @RequestMapping(value = "/my-profile-password", method = RequestMethod.POST)
    public ModelAndView updateProfilePassword(PasswordDTO passwordDTO, RedirectAttributes attributes) {
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = new ModelAndView("redirect:/my-profile");
        logger.debug("Updated profile password user " + user.getUsername());
        if (passwordEncoder.matches(passwordDTO.getPassword(), user.getPassword())) {
            if (passwordDTO.getMatchingPassword().length() >= 8) {
                user.setPassword(passwordEncoder.encode(passwordDTO.getMatchingPassword()));
                attributes.addFlashAttribute("password_updated", true);
            } else {
                attributes.addFlashAttribute("password_msg",
                        messageSource.getMessage("user_password.length", null, new Locale(user.getFirstLang().getCode())));
            }
        } else {
            attributes.addFlashAttribute("password_msg",
                    messageSource.getMessage("profile.wrong_password", null, new Locale(user.getFirstLang().getCode())));
        }
        return model;
    }

    /**
     * Update user private profile
     * @param userUpdate user updates
     * @param attributes redirect attributes
     * @return redirect to user profile
     */
    @Transactional
    @RequestMapping(value = "/my-profile-private", method = RequestMethod.POST)
    public ModelAndView updateUserPrivate(User userUpdate, RedirectAttributes attributes) {
        ModelAndView model = new ModelAndView("redirect:/my-profile");
        User user = Methods.getLoggedInUser(userService);
        logger.debug("Updated private profile user " + user.getUsername());
        String email = userUpdate.getEmail();
        if (email != null && userService.findByEmail(email) == null) {
            user.setEmail(email);
            historyService.createHistory(user, null, null, "Changed email", HistoryTypeEnum.EDIT_EMAIL, VisibleEnum.HIDDEN);
        }
        else if (!user.getEmail().equals(email)) {
            attributes.addFlashAttribute("email_exists", messageSource.getMessage("user_email.exists", null, new Locale(user.getFirstLang().getCode())));
            attributes.addFlashAttribute("saved_private", false);
            return model;
        }
        if (!user.getAgeRange().equals(userUpdate.getAgeRange())) {
            user.setAgeRange(userUpdate.getAgeRange());
            historyService.createHistory(user, null, null, "Changed age range", HistoryTypeEnum.EDIT_AGE_RANGE, VisibleEnum.HIDDEN);
        }
        if (userUpdate.getGender() != null && (user.getGender() == null || !user.getGender().equals(userUpdate.getGender()))) {
            user.setGender(userUpdate.getGender());
            historyService.createHistory(user, null, null, "Changed gender", HistoryTypeEnum.EDIT_GENDER, VisibleEnum.HIDDEN);
        }
        if (user.getThemes() != null && userUpdate.getThemes() != null && (user.getThemes().size() != userUpdate.getThemes().size() || !user.getThemes().containsAll(userUpdate.getThemes()))) {
            user.setThemes(userUpdate.getThemes());
            historyService.createHistory(user, null, null, "Changed themes", HistoryTypeEnum.EDIT_POI, VisibleEnum.HIDDEN);
        }
        if (user.getSkills() != null && userUpdate.getSkills() != null && (user.getSkills().size() != userUpdate.getSkills().size() || !user.getSkills().containsAll(userUpdate.getSkills()))) {
            user.setSkills(userUpdate.getSkills());
            historyService.createHistory(user, null, null, "Changed skills", HistoryTypeEnum.EDIT_SKILLS, VisibleEnum.HIDDEN);
        }
        elasticsearchPublisher.publishUser(user, ESType.UPDATE);
        attributes.addFlashAttribute("saved_private", true);
        return model;
    }

    /**
     * Update user public profile
     * @param userUpdate user updates
     * @param attributes redirect attributes
     * @return redirect to user profile
     */
    @Transactional
    @RequestMapping(value = "/my-profile-public", method = RequestMethod.POST)
    public ModelAndView updateUserPublic(User userUpdate, RedirectAttributes attributes) {
        ModelAndView model = new ModelAndView("redirect:/my-profile");
        User user = Methods.getLoggedInUser(userService);
        logger.debug("Updated public profile user " + user.getUsername());
        String username = userUpdate.getUsername();
        if (username != null && userService.findByUsername(username) == null) {
            user.setUsername(username);
            historyService.createHistory(user, null, null, "Changed username", HistoryTypeEnum.EDIT_USERNAME, VisibleEnum.HIDDEN);
        }
        else if (!user.getUsername().equals(username)) {
            attributes.addFlashAttribute("username_exists", messageSource.getMessage("user_username.exists", null, new Locale(userUpdate.getFirstLang().getCode())));
            attributes.addFlashAttribute("saved_public", false);
            return model;
        }
        if (!user.getProfile().equals(userUpdate.getProfile())) {
            user.setProfile(userUpdate.getProfile());
            if (user.getOrganisation() != null)
                user.setOrganisation(null);
            historyService.createHistory(user, null, null, "Changed profile", HistoryTypeEnum.EDIT_PROFILE, VisibleEnum.HIDDEN);
        }
        if (userUpdate.getDescription() != null && (user.getDescription() == null || !user.getDescription().equals(userUpdate.getDescription()))) {
            user.setDescription(userUpdate.getDescription());
            historyService.createHistory(user, null, null, "Changed description", HistoryTypeEnum.EDIT_DESCRIPTION, VisibleEnum.HIDDEN);
        }
        if (!user.getLocality().equals(userUpdate.getLocality())) {
            user.setLocality(userUpdate.getLocality());
            user.setParentLocality(userUpdate.getLocality().getLocalityParent());
            historyService.createHistory(user, null, null, "Changed locality", HistoryTypeEnum.EDIT_LOCALITY, VisibleEnum.HIDDEN);
        }
        if (!user.getFirstLang().equals(userUpdate.getFirstLang())) {
            user.setFirstLang(userUpdate.getFirstLang());
            historyService.createHistory(user, null, null, "Changed first language", HistoryTypeEnum.EDIT_FIRST_LANGUAGE, VisibleEnum.HIDDEN);
        }
        if (userUpdate.getSecondLang() != null && (user.getSecondLang() == null || !user.getSecondLang().equals(userUpdate.getSecondLang()))) {
            user.setSecondLang(userUpdate.getSecondLang());
            historyService.createHistory(user, null, null, "Changed second language", HistoryTypeEnum.EDIT_SECOND_LANGUAGE, VisibleEnum.HIDDEN);
        }
        elasticsearchPublisher.publishUser(user, ESType.UPDATE);
        attributes.addFlashAttribute("saved_public", true);
        return model;
    }

    /**
     * Display user organisation
     * @return organisation mav
     */
    @RequestMapping(value = {"/organisation", "/organisation.html"}, method = RequestMethod.GET)
    public ModelAndView showOrganisation() {
        logger.debug("User Organisation page");
        User user = Methods.getLoggedInUser(userService);
        // If user is not a service provider can't reach organisation page
        if (!user.getProfile().equals(ProfileEnum.SERVICE_PROVIDER))
            throw new NotServiceProviderException(messageSource.getMessage("organisation.not_service_provider", null, new Locale(user.getFirstLang().getCode())));
        ModelAndView model = createUserMV(user, "organisation");
        Organisation org = user.getOrganisation() == null ? new Organisation() : user.getOrganisation();
        model.addObject("organisation", org);
        model.addObject("newOrganisation", org);
        model.addObject("members", user.getOrganisation() == null ? new LinkedList<>() : userService.getOrganisationMembers(org));
        model.addObject("localities", localityRepository.findAllByLocalityParentNotNull());
        model.addObject("languages", languageRepository.findAll());
        model.addObject("newService", new Service());
        model.addObject("services", user.getOrganisation() == null ? new LinkedList<>() : serviceService.findByOrganisationApproved(org));
        model.addObject("themes", themeRepository.findAll());
        return model;
    }

    /**
     * Create new organisation
     * @param organisation organisation
     * @param bindingResult binding result
     * @param attributes redirect attributes
     * @return organisation mav
     */
    @RequestMapping(value = "/organisation", method = RequestMethod.POST)
    public ModelAndView createOrganisation(@Valid Organisation organisation, BindingResult bindingResult,  RedirectAttributes attributes) {
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = showOrganisation();
        Organisation existingOrg = organisationService.findByName(organisation.getName());
        // Organisation already exists and it is not the user one
        if (existingOrg != null && (user.getOrganisation() == null || user.getOrganisation().getId() != existingOrg.getId())) {
            bindingResult.reject("organisation.name", "exists");
            model.addObject("org_exists", messageSource.getMessage("organisation.exists", null, new Locale(user.getFirstLang().getCode())));
        }
        if (!bindingResult.hasErrors()) {
            // Update organisation
            if (user.getOrganisation() != null) {
                logger.debug("Update organisation fields " + organisation.getName() + " by user " + user.getUsername());
                user.getOrganisation().setName(organisation.getName());
                user.getOrganisation().setStructure(organisation.getStructure());
                user.getOrganisation().setWebsite(organisation.getWebsite());
                organisationService.save(user.getOrganisation());
                attributes.addFlashAttribute("updated", true);
            } else {
                // Create organisation
                logger.debug("Create organisation " + organisation.getName() + " by user " + user.getUsername());
                organisationService.createOrganisation(organisation, user);
            }
            model = new ModelAndView("redirect:/organisation");
        }
        return model;
    }

    /**
     * Display facilitator page
     * @return facilitator mav
     */
    @PreAuthorize("hasRole('ROLE_FACILITATOR')")
    @RequestMapping(value = {"/facilitator", "/facilitator.html"}, method = RequestMethod.GET)
    public ModelAndView showFacilitator() throws IllegalAccessException {
        logger.debug("Facilitator page");
        User user = Methods.getLoggedInUser(userService);
        if (!user.isFacilitator()) throw new IllegalAccessException();
        ModelAndView model = createUserMV(user, "facilitator");
        String secondLang = user.getSecondLang() == null ? null : user.getSecondLang().getCode();
        model.addObject("suggested_groups", groupService.findSuggestedGroups(user.getLocality().getId(),
                user.getParentLocality().getId(), user.getFirstLang().getCode(), secondLang));
        model.addObject("groups", groupService.findAllInProcess(user.getLocality().getId(),
                user.getParentLocality().getId(), user.getFirstLang().getCode(), secondLang));
        return model;
    }

    /**
     * Display moderator page
     * @return moderator mav
     */
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @RequestMapping(value = {"/moderator", "/moderator.html"}, method = RequestMethod.GET)
    public ModelAndView showModerator() throws IllegalAccessException {
        logger.debug("Moderator page");
        User user = Methods.getLoggedInUser(userService);
        if (!user.isModerator()) throw new IllegalAccessException();
        ModelAndView model = createUserMV(user, "moderator");
        model.addObject("reports", reportService.findAllPending());
        model.addObject("banned_users", userService.getBannedUsers());
        model.addObject("org_proposals", postService.findOrgProposals());
        String secondLang = user.getSecondLang() == null ? null : user.getSecondLang().getCode();
        model.addObject("services", serviceService.findAllSuggested(user.getLocality().getId(),
                user.getParentLocality().getId(), user.getFirstLang().getCode(), secondLang));
        addKnowledgeBaseInsights(model, user);
        return model;
    }

    /*private List<UserReports> getUserReports() {
        List<UserReports> userReports = new LinkedList<>();
        List<User> reportedUsers = reportService.findAllReporteds();
        for (User reported : reportedUsers) {
            userReports.add(new UserReports(reported, reportService.findByReported(reported)));
        }
        return userReports;
    }*/

    /**
     * Attach knowledge base insights to the model
     * @param model model
     * @param user user
     */
    private void addKnowledgeBaseInsights(ModelAndView model, User user) {
        model.addObject("views_joins_groups", restAPICaller.getMostViewedAndJoinedGroups());
        model.addObject("posts_groups", restAPICaller.getMostPostedInGroups());
        model.addObject("trending_groups", restAPICaller.getMostTrendingGroups());
        model.addObject("popular_themes", restAPICaller.getPopularThemes());
        model.addObject("popular_groups_language", restAPICaller.getPopularGroupsByLanguage(user.getFirstLang().getId()));
        model.addObject("popular_groups_locality", restAPICaller.getPopularGroupsByLocality(user.getLocality().getId()));
    }

    /**
     * Display user topics
     * @param createSize created topics size
     * @param contributeSize contributed topics size
     * @param subscribeSize subscribed topics size
     * @return user topics mav
     */
    @RequestMapping(value = {"/topics-posted", "/topics-posted.html"}, method = RequestMethod.GET)
    public ModelAndView showTopicsPostedByUser(@RequestParam("create_size") Optional<Integer> createSize,
                                               @RequestParam("contribute_size") Optional<Integer> contributeSize,
                                               @RequestParam("subscribe_size") Optional<Integer> subscribeSize) {
        logger.debug("User topics page");
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = createUserMV(user, "topics-posted");
        model.addObject("topicsCreated", groupService.findGroupsByUserIdAndRelation(user.getId(), GroupUserRelationEnum.CREATED, 0, createSize.orElse(Constants.TOPIC_CREATE_PAGE_SIZE)));
        model.addObject("topicsContributed", groupService.findGroupsByUserIdAndRelation(user.getId(), GroupUserRelationEnum.CONTRIBUTOR, 0, contributeSize.orElse(Constants.TOPIC_SUBSCRIBE_PAGE_SIZE)));
        model.addObject("topicsSubscribed", groupService.findGroupsByUserIdAndRelation(user.getId(), GroupUserRelationEnum.SUBSCRIBED, 0, subscribeSize.orElse(Constants.TOPIC_SUBSCRIBE_PAGE_SIZE)));
        return model;
    }

    /**
     * Display user chats
     * @param page page
     * @param size size
     * @return chats mav
     */
    @RequestMapping(value = {"/messages", "/messages.html"}, method = RequestMethod.GET)
    public ModelAndView showUserChats(@RequestParam("page") Optional<Integer> page,
                                      @RequestParam("size") Optional<Integer> size) {
        logger.debug("User chats page");
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = createUserMV(user, "messages");
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(Constants.MESSAGES_PAGE_SIZE);
        Page<Chat> chatsPage = chatService.findChats(user.getId(), currentPage, pageSize); // TODO CHeck
        model.addObject("chatsPage", chatsPage);
        int totalPages = chatsPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addObject("pageNumbers", pageNumbers);
        }
        return model;
    }

    /**
     * Display chat with a user
     * @param username username
     * @param page page
     * @param size size
     * @return chat mav
     */
    @RequestMapping(value = {"/messages/{username}", "/messages.html/{username}"}, method = RequestMethod.GET)
    public ModelAndView showChatMessages(@PathVariable(value = "username") String username,
                                         @RequestParam("page") Optional<Integer> page,
                                         @RequestParam("size") Optional<Integer> size) {
        User user = Methods.getLoggedInUser(userService);
        if (username.equals(user.getUsername())) return new ModelAndView("errors/error", "error", messageSource.getMessage("error.self_chat", null, new Locale(user.getFirstLang().getCode())));
        User receiver = userService.findByUsername(username);
        if (receiver == null || receiver.isAnonymized()) throw new UsernameNotFoundException(username);
        logger.debug("User chat page with user " + receiver.getUsername());
        Chat chat = chatService.findChatBetween(user, receiver);
        chat.updateLastSeen(user);
        chatService.save(chat);
        List<MessageWithDocuments> messages = new LinkedList<>();
        // Merge Message with its Documents
        for (Message m: chatService.findAllMessages(chat, 0, 500)) // TODO 500?
            messages.add(new MessageWithDocuments(m, documentService.findAllByMessage(m)));
        ModelAndView model = showUserChats(page, size);
        model.addObject("chat", chat);
        model.addObject("receiver", receiver);
        model.addObject("messages", messages);
        model.addObject("message", new Message());
        return model;
    }

    /**
     * Send a message to a user
     * @param message message
     * @param bindingResult binding result
     * @param username username
     * @param files attached files
     * @param request servlet request
     * @return redirect to chat between that user
     */
    @RequestMapping(value = {"/messages/{username}", "/messages.html/{username}"}, method = RequestMethod.POST)
    public String sendMessage(@Valid Message message, BindingResult bindingResult,
                              @PathVariable(value = "username") String username,
                              @RequestParam("file") Optional<MultipartFile[]> files,
                              HttpServletRequest request) {
        User sender = Methods.getLoggedInUser(userService);
        User receiver = userService.findByUsername(username);
        logger.debug("Send message from " + sender.getUsername() + " to " + receiver.getUsername());
        Chat chat = chatService.findChatBetween(sender, receiver);
        if (!bindingResult.hasErrors()) {
            Message msg = chatService.createMessage(chat, sender, message.getText(), request);
            logger.debug("Sent message from " + sender.getUsername() + " to " + receiver.getUsername());
            if (files.isPresent()) {
                for (MultipartFile file: files.get())
                    if (!file.isEmpty())
                        documentService.storeFile(file, null, null, msg.getId(), null, null, null, new Locale(sender.getFirstLang().getCode()));
            }
        }
        return "redirect:/messages/" + receiver.getUsername();
    }

    /**
     * Display user notifications
     * @param size size
     * @return notifications mav
     */
    @RequestMapping(value = {"/notifications", "/notifications.html"}, method = RequestMethod.GET)
    public ModelAndView showUserNotifications(@RequestParam("size") Optional<Integer> size) {
        logger.debug("User notifications page");
        User user = Methods.getLoggedInUser(userService);
        int currentPage = 0;
        int pageSize = size.orElse(Constants.NOTIFICATION_PAGE_SIZE);
        Page<Notification> notificationsPage = notificationService.userNotifications(user.getId(), currentPage, pageSize);
        ModelAndView model = createUserMV(user, "notifications");
        model.addObject("notificationsPage", notificationsPage);
        model.addObject("notifyByEmail", user.getNotifyByEmail().equals(OnOffEnum.ON));
        model.addObject("notifyNewTopic", user.getNotifyMessageByEmail().equals(OnOffEnum.ON));
        model.addObject("notifyAll", user.getNotifyAll().equals(OnOffEnum.ON));
        return model;
    }

    /**
     * Display history
     * @param size size
     * @return hisotry mav
     */
    @RequestMapping(value = {"/history", "/history.html"}, method = RequestMethod.GET)
    public ModelAndView showUserHistory(@RequestParam("size") Optional<Integer> size) {
        logger.debug("User history page");
        User user = Methods.getLoggedInUser(userService);
        int currentPage = 0;
        int pageSize = size.orElse(Constants.HISTORY_PAGE_SIZE);
        Page<History> historyPage = historyService.getUserHistory(user, currentPage, pageSize);
        ModelAndView model = createUserMV(user, "history");
        model.addObject("historyPage", historyPage);
        return model;
    }

    /**
     * Display user data
     * @return data mav
     */
    @RequestMapping(value = {"/my-data", "/my-data.html"}, method = RequestMethod.GET)
    public ModelAndView showUserData() {
        logger.debug("User data page");
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = createUserMV(user, "my-data");
        return model;
    }

    /**
     * View user data
     * @return PDF with user data
     * @throws DocumentException error creating pdf
     */
    @RequestMapping(value = {"/my-data/view"})
    public ResponseEntity<Resource> viewData() throws DocumentException {
        User user = Methods.getLoggedInUser(userService);
        String filename = user.getUsername() + ".pdf";

        ByteArrayOutputStream outputStream = pdfGenerator.generatePDF(user, filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(new ByteArrayResource(outputStream.toByteArray()));
    }

    /**
     * Download user data
     * @return JSON with user data
     * @throws JsonProcessingException error processing json
     */
    @RequestMapping(value = {"/my-data/download"})
    public ResponseEntity<String> downloadData() throws JsonProcessingException {
        User user = Methods.getLoggedInUser(userService);
        String filename = user.getUsername() + ".json";

        ObjectMapper mapper = new ObjectMapper();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(mapper.writeValueAsString(new UserData(user)));
    }

    /**
     * Deactivate account, turns all user data and contributions anonymous until log in again
     * @return logout
     */
    @RequestMapping(value = {"/my-data/deactivate"})
    public String deactivateAccount() {
        User user = Methods.getLoggedInUser(userService);
        userService.anonymise(user);
        logger.debug("Deactivated account of " + user.getUsername());
        return "redirect:/logout";
    }

    /**
     * Delete account, turn all user data and contributions anonymous
     * @return logout
     */
    @RequestMapping(value = {"/my-data/delete"})
    public String deleteAccount() {
        User user = Methods.getLoggedInUser(userService);
        userService.delete(user);
        logger.debug("Deleted account of " + user.getUsername());
        return "redirect:/logout";
    }

    /**
     * Save user notifications changes
     * @param notifyByEmail notify by email
     * @param notifyNewTopic notify when new topic is proposed
     * @param notifyAll notify always
     */
    @RequestMapping(value = {"/notifications/save-user"})
    @ResponseStatus(HttpStatus.OK)
    public void saveUserNotificationOptions(@RequestParam("notifyByEmail") Optional<Boolean> notifyByEmail,
                                            @RequestParam("notifyNewTopic") Optional<Boolean> notifyNewTopic,
                                            @RequestParam("notifyAll") Optional<Boolean> notifyAll) {
        logger.debug("Save notifications options");
        User user = Methods.getLoggedInUser(userService);
        notifyByEmail.ifPresent(aBoolean -> user.setNotifyByEmail(aBoolean ? OnOffEnum.ON : OnOffEnum.OFF));
        notifyNewTopic.ifPresent(aBoolean -> user.setNotifyMessageByEmail(aBoolean ? OnOffEnum.ON : OnOffEnum.OFF));
        notifyAll.ifPresent(aBoolean -> user.setNotifyAll(aBoolean ? OnOffEnum.ON : OnOffEnum.OFF));
        userService.save(user);
    }

    /**
     * Notification clicked
     * @param id notification id
     * @return redirect to the notification url
     */
    @RequestMapping(value = "/notifications/click/{notification_id}", method = RequestMethod.GET)
    public String notificationClicked(@PathVariable(value = "notification_id") Integer id) {
        Notification notification = notificationService.getNotificationById(id);
        notificationService.readNotification(notification);
        logger.debug("Clicked notification " + id + " of user " + notification.getUser().getUsername());
        return "redirect:" + notification.getUrl();
    }

    /**
     * Notification read
     * @param id notification id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/notifications/read/{notification_id}")
    public void notificationRead(@PathVariable(value = "notification_id") Integer id) {
        Notification notification = notificationService.getNotificationById(id);
        notificationService.readNotification(notification);
        logger.debug("Read notification " + id + " of user " + notification.getUser().getUsername());
    }

    /**
     * Read all user notifications
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/notifications/read_all")
    public void notificationReadAll() {
        User user = Methods.getLoggedInUser(userService);
        notificationService.readAllNotifications(user);
        logger.debug("All notification read of user " + user.getUsername());
    }

    /**
     * Delete all user notifications
     */
    @Transactional
    @RequestMapping("/notifications/delete_all")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllNotifications() {
        User user = Methods.getLoggedInUser(userService);
        notificationService.deleteAll(user);
        logger.debug("All notification deleted of user " + user.getUsername());
    }

    /**
     * Delete a notification
     * @param id notification id
     */
    @RequestMapping("/notifications/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteNotification(@PathVariable(value = "id") Integer id) {
        User user = Methods.getLoggedInUser(userService);
        notificationService.delete(id, user);
        logger.debug("Notification deleted " + id + " of user " + user.getUsername());
    }

    /**
     * Delete a history
     * @param id history id
     */
    @RequestMapping("/history/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteHistory(@PathVariable(value = "id") Integer id) {
        User user = Methods.getLoggedInUser(userService);
        History history = historyService.findById(id);
        historyService.delete(history);
        logger.debug("History deleted " + id + " of user " + user.getUsername());
    }

    /**
     * Delete all user history
     */
    @Transactional
    @RequestMapping("/history/delete_all")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllHistory() {
        User user = Methods.getLoggedInUser(userService);
        historyService.deleteAllByUser(user);
        logger.debug("All history deleted of user " + user.getUsername());
    }

    /**
     * Main sidebar view attributes
     * @param user Logged in User
     * @param viewName view name to be displayed
     * @return ModelAndView with user attributes and corresponding view
     */
    private ModelAndView createUserMV(User user, String viewName) {
        ModelAndView model = new ModelAndView(viewName);
        model.addObject("user", user);
        model.addObject("lang_code", user.getFirstLang().getCode());
        model.addObject("localityName", messageSource.getMessage(user.getLocality().getName().toLowerCase(),null, new Locale(user.getFirstLang().getCode())));
        model.addObject("languageName", messageSource.getMessage(user.getFirstLang().getName().toLowerCase(),null, new Locale(user.getFirstLang().getCode())));
        model.addObject("percentage", user.calculateProfileCompletion() + "%");
        Long numNotif = notificationService.countUnreadNotifications(user);
        Long numMessages = chatService.countUnreadChats(user.getId());
        model.addObject("newNotifNumber", numNotif);
        model.addObject("newMessageNumber", numMessages);
        model.addObject("dashboardNumber", numNotif + numMessages);
        return model;
    }

    /**
     * Find username to chat
     * @param searchString string to search like username
     * @return return list of  existing usernames
     */
    @RequestMapping(value = "/username/search")
    public @ResponseBody List<String> findUsernameLike(@RequestParam("searchedString") String searchString) {
        User user = Methods.getLoggedInUser(userService);
        return (List<String>) userService.findByUsernameLike("%" + searchString + "%", user.getUsername());
    }

    /**
     * Check if there are new messages in the chat with a user
     * @param id chat id
     * @return true or false
     */
    @RequestMapping(value = "/messages/{chat_id}/are-new-messages")
    public @ResponseBody Boolean areThereNewMessages(@PathVariable(value = "chat_id") Integer id) {
        User user = Methods.getLoggedInUser(userService);
        Chat chat = chatService.findChatById(id);
        return chat.hasNewMessages(user);
    }

    /**
     * Send a message to user from profile pop-up
     * @param senderId sender id
     * @param receiverId receiver id
     * @param message message
     * @param request servlet request
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/messages/contact")
    public void createNewMessage(@RequestParam(value = "sender") Integer senderId,
                                 @RequestParam(value = "receiver") Integer receiverId,
                                 @RequestParam(value = "message") String message,
                                 HttpServletRequest request) {
        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);
        Chat chat = chatService.findChatBetween(sender, receiver);
        chatService.createMessage(chat, sender, message, request);
    }

    /**
     * Display explore services
     * @param locale Locale
     * @param query optional query
     * @return explore services mav
     */
    @RequestMapping(value = {"/explore-services", "/explore-services.html"}, method = RequestMethod.GET)
    public ModelAndView exploreServices(Locale locale,
                                  @RequestParam("query") Optional<String> query) {
        // TODO PREVENT SQL INJECTION | TRAVERSAL PATH ATTACK
        // Only a-zA-Z, handle metacharacters...
        logger.debug("Explore services");
        List<Service> services;
        ModelAndView model = new ModelAndView("explore-services");
        Long numNotif = 0L;
        CompletableFuture<ResponseEntity<List<ServiceByKeywords>>> servicesFuture = null;

        /*if (query.isPresent()) {
            servicesFuture = restAPICaller.futureSearchServicesByTopics(query.get(), locale.getLanguage());
        }

        try {
            // Logged in users
            User user = Methods.getLoggedInUser(userService);
            model.addObject("user", user);
            numNotif = notificationService.countUnreadNotifications(user);
            model.addObject("lang_code", user.getFirstLang().getCode());
            query.ifPresent(q -> historyService.createHistory(user, null, null, q.trim(), HistoryTypeEnum.SEARCH_SERVICE, VisibleEnum.HIDDEN));
            services = serviceService.findByLocalityAndLanguage(user.getLocality().getId(), user.getParentLocality().getId(), user.getFirstLang().getCode(), user.getSecondLang()!=null?user.getSecondLang().getCode():null);
        } catch (ClassCastException e) {
            // Not logged in users
            model.addObject("lang_code", methods.getLanguageCodeFromLocale(locale));
            services = serviceService.findByLanguage(locale.getLanguage());
        }
        if (query.isPresent()) {
            services = mergeServices(services, retrieveRelatedServices(servicesFuture));
        }*/
        if (query.isPresent()) {
            servicesFuture = restAPICaller.futureSearchServicesByKeywords(Arrays.asList(query.get().trim().split(" ")), locale.getLanguage(), 255);
        }

        try {
            // Logged in users
            User user = Methods.getLoggedInUser(userService);
            model.addObject("user", user);
            numNotif = notificationService.countUnreadNotifications(user);
            model.addObject("lang_code", user.getFirstLang().getCode());
            if (query.isPresent()) {
                historyService.createHistory(user, null, null, query.get().trim(), HistoryTypeEnum.SEARCH_SERVICE, VisibleEnum.HIDDEN);
                services = retrieveRelatedServices(servicesFuture);
            } else
                services = serviceService.findByLocalityAndLanguage(user.getLocality().getId(), user.getParentLocality().getId(), user.getFirstLang().getCode(), user.getSecondLang()!=null?user.getSecondLang().getCode():null);
        } catch (ClassCastException e) {
            // Not logged in users
            model.addObject("lang_code", methods.getLanguageCodeFromLocale(locale));
            if (query.isPresent())
                services = retrieveRelatedServices(servicesFuture);
            else
                services = serviceService.findByLanguage(locale.getLanguage());
        }

        model.addObject("services", services);
        model.addObject("query", "");
        model.addObject("themes", themeRepository.findAll());
        model.addObject("newNotifNumber", numNotif);
        return model;
    }

    /**
     * Retrieve future with the services of the query
     * @param relatedServicesFuture future
     * @return services
     */
    private List<Service> retrieveRelatedServices(CompletableFuture<ResponseEntity<List<ServiceByKeywords>>> relatedServicesFuture) {
        try {
            ResponseEntity<List<ServiceByKeywords>> response = relatedServicesFuture.get();
            List<ServiceByKeywords> body = response.getBody();
            if (response.getStatusCode().value() == 200 &&
                    body != null &&
                    !body.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                List<ServiceByKeywords> servicesList = mapper.convertValue(body, new TypeReference<List<ServiceByKeywords>>(){});
                // TODO: add .filter(x -> x.getLocality_id() == locality_id)
                List<Integer> servicesIds = servicesList.stream().map(ServiceByKeywords::getService_id).
                        collect(Collectors.toList());
                return serviceService.findAllByIds(servicesIds);
            }
        } catch (Exception e) {
            logger.error("Knowledge Base error", new KnowledgeBaseException(e));
        }
        return new LinkedList<>();
    }

    /**
     * Merge two list of services, without duplicates
     * @param list1 list 1 of services
     * @param list2 list 2 of services
     * @return single list of services
     */
    private List<Service> mergeServices(List<Service> list1, List<Service> list2) {
        Set<Service> set = new LinkedHashSet<>(list1);
        set.addAll(list2);
        return new ArrayList<>(set).stream().
                filter(Service::isApproved)
               .collect(Collectors.toList());
    }

}
