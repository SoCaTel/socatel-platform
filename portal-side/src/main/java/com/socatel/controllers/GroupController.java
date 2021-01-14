package com.socatel.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socatel.components.Methods;
import com.socatel.configurations.ThreadPoolTaskSchedulerConfig;
import com.socatel.dtos.FeedbackWithContributions;
import com.socatel.dtos.QuestionAndAnswers;
import com.socatel.exceptions.GroupNotAccessibleException;
import com.socatel.exceptions.GroupNotFoundException;
import com.socatel.exceptions.KnowledgeBaseException;
import com.socatel.models.*;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.repositories.*;
import com.socatel.rest_api.RestAPICaller;
import com.socatel.rest_api.rdi.UserActivity;
import com.socatel.rest_api.rec.SimilarService;
import com.socatel.rest_api.sdi.external_service.ServiceByTopic;
import com.socatel.rest_api.sdi.external_service.ServicesList;
import com.socatel.services.answer.AnswerService;
import com.socatel.services.chat.ChatService;
import com.socatel.services.document.DocumentService;
import com.socatel.services.email.EmailService;
import com.socatel.services.feedback.FeedbackService;
import com.socatel.services.group.GroupService;
import com.socatel.services.history.HistoryService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.proposition.PropositionService;
import com.socatel.services.question.QuestionService;
import com.socatel.services.service.ServiceService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class GroupController {

    @Autowired private GroupService groupService;
    @Autowired private GroupRepository groupRepository;
    @Autowired private PostService postService;
    @Autowired private PropositionService propositionService;
    @Autowired private UserService userService;
    @Autowired private NotificationService notificationService;
    @Autowired private ThemeRepository themeRepository;
    @Autowired private ChatService chatService;
    @Autowired private MessageSource messageSource;
    @Autowired private ThreadPoolTaskSchedulerConfig.GroupScheduler groupScheduler;
    @Autowired private LocalityRepository localityRepository;
    @Autowired private DocumentService documentService;
    @Autowired private HistoryService historyService;
    @Autowired private RestAPICaller restAPICaller;
    @Autowired private FeedbackService feedbackService;
    @Autowired private UserFeedbackVoteRepository userFeedbackVoteRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private Methods methods;
    @Autowired private AnswerService answerService;
    @Autowired private QuestionService questionService;
    @Autowired private ServiceService serviceService;
    @Autowired private EmailService emailService;

    private ObjectMapper mapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(GroupController.class);

    /**
     * Main top view common in topics
     * @param user Logged in User
     * @param viewName view name to be displayed
     * @return ModelAndView with user attributes and corresponding view
     */
    private ModelAndView createGroupModel(Optional<User> user, String viewName, Locale locale) {
        ModelAndView model = new ModelAndView(viewName);
        Long numNotif = 0L;
        if (user.isPresent()) {
            model.addObject("user", user.get());
            numNotif = notificationService.countUnreadNotifications(user.get());
            model.addObject("lang_code", user.get().getFirstLang().getCode());
        } else {
            model.addObject("lang_code", methods.getLanguageCodeFromLocale(locale));
        }
        model.addObject("newNotifNumber", numNotif);
        return model;
    }

    /**
     * Display topic create page
     * @return topic create mav
     */
    @RequestMapping(value = {"/topic-create", "/topic-create.html"}, method = RequestMethod.GET)
    public ModelAndView topicCreation(Locale locale) {
        logger.debug("Topic creation");
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = createGroupModel(Optional.of(user), "topic-create", locale);
        model.addObject("group", new Group());
        model.addObject("allThemes", themeRepository.findAll());
        model.addObject("panEuropean", localityRepository.findPanEuropean());
        return model;
    }

    /**
     * Create new topic
     * @param files attached files
     * @param group new group
     * @param bindingResult bindingResult
     * @return topic create mav
     */
    @RequestMapping(value = {"/topic-create", "/topic-create.html"}, method = RequestMethod.POST)
    public ModelAndView createTopic(@RequestParam("file") Optional<MultipartFile[]> files,
                                    @Valid Group group,
                                    BindingResult bindingResult,
                                    Locale locale) {
        logger.debug("Create topic");
        User user = Methods.getLoggedInUser(userService);
        ModelAndView model = createGroupModel(Optional.of(user), "topic-create", locale);
        if (group.getThemes().isEmpty()) {
            bindingResult.reject("themes");
            model.addObject("notAllSelected", messageSource.getMessage("create_topic.emtpy_themes", null, new Locale(user.getFirstLang().getCode())));
        }
        if (group.getLanguage() == null) {
            bindingResult.reject("language");
        }
        if (group.getLocality() == null) {
            bindingResult.reject("locality");
        }
        if (!bindingResult.hasErrors() && group.getLocality().getId() == 999 && !group.getLanguage().getCode().equalsIgnoreCase("en")) {
            bindingResult.reject("paneuropean");
            model.addObject("onlyEnglish", messageSource.getMessage("create_topic.only_english", null, new Locale(user.getFirstLang().getCode())));
        }
        if (!bindingResult.hasErrors()) {
            group.setTimestamp(new Timestamp(System.currentTimeMillis()));
            group.setInitiator(user);
            group.setStatus(GroupStatusEnum.SUGGESTED);
            groupService.createNewGroup(group, user);
            if (files.isPresent()) {
                for (MultipartFile file: files.get())
                    if (!file.isEmpty())
                        documentService.storeFile(file, group.getId(), null, null, null, null, null, new Locale(user.getFirstLang().getCode()));
            }
            notificationService.notifyModerators("notification.moderator.suggested_topic", "/moderator", group.getName());
            model.addObject("success", true);
            model.addObject("allThemes", themeRepository.findAll());
            model.addObject("group", new Group());
            model.addObject("panEuropean", localityRepository.findPanEuropean());
            logger.debug("Topic created " + group.getId() + " " + group.getName());
        } else {
            model.addObject("allThemes", themeRepository.findAll());
            model.addObject("panEuropean", localityRepository.findPanEuropean());
        }
        return model;
    }

    /**
     * Display topic find
     * @param locale Locale
     * @param query optional query
     * @param sorting optional sorting
     * @return topic find mav
     */
    @RequestMapping(value = {"/topic-find", "/topic-find.html"}, method = RequestMethod.GET)
    public ModelAndView topicFind(Locale locale,
                                  @RequestParam("query") Optional<String> query,
                                  @RequestParam("sorting") Optional<Integer> sorting) {
        // TODO PREVENT SQL INJECTION | TRAVERSAL PATH ATTACK
        // Only a-zA-Z, handle metacharacters...
        logger.debug("Topic find");
        ModelAndView model;
        List<Group> groups;
        CompletableFuture<ResponseEntity<ServicesList>> services = null;
        if (query.isPresent()) {
            services = restAPICaller.futureSearchServicesByTopics(query.get(), locale.getLanguage(), 4);
        }
        try {
            // Logged in users
            User user = Methods.getLoggedInUser(userService);
            query.ifPresent(q -> historyService.createHistory(user, null, null, q.trim(), HistoryTypeEnum.SEARCH_TOPIC, VisibleEnum.HIDDEN));
            model = createGroupModel(Optional.of(user), "topic-find", locale);
            groups = searchGroupsLoggedIn(model, query, sorting, user);
        } catch (ClassCastException e) {
            // Not logged in users
            model = createGroupModel(Optional.empty(), "topic-find", locale);
            groups = searchGroupsAnonymous(query, sorting, locale);
        }
        model.addObject("query", "");
        model.addObject("themes", themeRepository.findAll());
        addGroupsData(groups, model, locale);
        if (query.isPresent())
            model.addObject("services", retrieveRelatedServices(services));
        return model;
    }

    /**
     * Search groups by a logged in user
     * @param model model
     * @param query optional query
     * @param sorting optional sorting
     * @param user logged in user
     * @return groups
     */
    private List<Group> searchGroupsLoggedIn(ModelAndView model, Optional<String> query, Optional<Integer> sorting, User user) {
        logger.debug("Topic find - logged in " + user.getId() + " " + user.getUsername());
        List<GroupUserRelation> relations = new LinkedList<>();
        List<Group> kbGroups = new LinkedList<>();
        List<Group> groups = groupService.findGroupsLoggedIn(query, sorting, user.getLocality().getId(), user.getParentLocality().getId(), user.getFirstLang().getCode(), user.getSecondLang()!=null?user.getSecondLang().getCode():null);
        if (query.isPresent()) {
            kbGroups = restAPICaller.getGroupsGivenKeyword(query.get(), user.getFirstLang().getCode(), user.getLocality().getId());
        }
        groups = mergeGroups(groups, kbGroups, sorting);
        for (Group group : groups) {
            relations.add(groupService.findGroupRelation(group, user));
        }
        model.addObject("relations", relations);
        return groups;
    }

    /**
     * Search groups by an anonymous user
     * @param query optional query
     * @param sorting optional sorting
     * @param locale Locale
     * @return groups
     */
    private List<Group> searchGroupsAnonymous(Optional<String> query, Optional<Integer> sorting, Locale locale) {
        logger.debug("Topic find - anonymous");
        List<Group> kbGroups = new LinkedList<>();
        List<Group> groups = groupService.findGroupsAnonymously(query, sorting, locale.getLanguage());
        if (query.isPresent()) {
            kbGroups = restAPICaller.getGroupsGivenKeyword(query.get(), locale.getLanguage(), null);
        }
        return mergeGroups(groups, kbGroups, sorting);
    }

    /**
     * Attach groups and their data to the model
     * @param groups groups
     * @param model model
     * @param locale locale
     */
    private void addGroupsData(List<Group> groups, ModelAndView model, Locale locale) {
        List<Long> participants = new LinkedList<>();
        List<Long> subscribers = new LinkedList<>();
        List<String> timeRemaining = new LinkedList<>();
        for (Group group : groups) {
            participants.add(groupService.countContributors(group.getId()));
            subscribers.add(groupService.countSubscribers(group.getId()));
            timeRemaining.add(methods.timeRemaining(group, locale));
        }
        model.addObject("groups", groups);
        model.addObject("timeRemaining", timeRemaining);
        model.addObject("participants", participants);
        model.addObject("subscribers", subscribers);
    }

    /**
     * Merge two list of groups, without duplicates and sorting
     * @param list1 list 1 of groups
     * @param list2 list 2 of groups
     * @param sorting optional sorting
     * @return single list of groups
     */
    private List<Group> mergeGroups(List<Group> list1, List<Group> list2, Optional<Integer> sorting) {
        Set<Group> set = new LinkedHashSet<>(list1);
        set.addAll(list2);
        boolean asc = false;
        if (sorting.isPresent()) {
            asc = sorting.get() == 3;
        }
        return new ArrayList<>(set).stream().
                filter(Group::isInProcessOrCompleted).
                sorted(
                        asc?
                                Comparator.comparing(Group::getTimestamp)
                                :
                                Comparator.comparing(Group::getTimestamp).reversed()
                ).collect(Collectors.toList());
    }

    /**
     * Display group
     * @param locale locale
     * @param groupId group id
     * @return group mav
     */
    @RequestMapping(value = {"/topic/{group_id}", "/topic.html/{group_id}"}, method = RequestMethod.GET)
    public ModelAndView showTopic(Locale locale,
                                  @PathVariable(value = "group_id") Integer groupId) {

        ModelAndView model;
        Group group = groupService.findById(groupId);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));

        switch (group.getStatus()) {
            case IDEATION:
                model = showTopicStep1(locale, groupId);
                break;
            case VALIDATION:
                model = showTopicStep2(locale, groupId);
                break;
            case CODESIGN:
                model = showTopicStep3(locale, groupId);
                break;
            case TEST:
                model = showTopicStep4(locale, groupId);
                break;
            case COMPLETED:
                model = showTopicStep5(locale, groupId);
                break;
            default:
                // Do not show REJECTED or SUGGESTED groups
                throw new GroupNotFoundException(String.valueOf(groupId));
        }
        return model;
    }

    /**
     * Display group step 1
     * @param locale locale
     * @param groupId group id
     * @return step 1 mav
     */
    @RequestMapping(value = {"/topic/{group_id}/step1", "/topic.html/{group_id}/step1"}, method = RequestMethod.GET)
    public ModelAndView showTopicStep1(Locale locale,
                                  @PathVariable(value = "group_id") Integer groupId) {
        Group group = groupService.findById(groupId);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        List<Post> ideas = postService.findAllGroupIdeas(groupId);
        ModelAndView model = createGroupStepsModel(group, "topic-step1", ideas, locale);
        // Specific for step 1
        model.addObject("newPost", new Post());
        List<Post> posts = postService.findPostsByGroupIdAndPostPhase(group.getId(), PostPhaseEnum.IDEATION);
        model.addObject("posts", posts);
        return model;
    }

    /**
     * Display group step 2
     * @param locale locale
     * @param groupId group id
     * @return step 2 mav
     */
    @RequestMapping(value = {"/topic/{group_id}/step2", "/topic.html/{group_id}/step2"}, method = RequestMethod.GET)
    public ModelAndView showTopicStep2(Locale locale,
                                       @PathVariable(value = "group_id") Integer groupId
                                       ) {
        Group group = groupService.findById(groupId);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        // Step not reached yet
        if (group.getStatus().compareTo(GroupStatusEnum.VALIDATION) < 0)
            return showTopic(locale, groupId);
        List<Post> ideas = postService.findGroupBestIdea(groupId);
        ModelAndView model = createGroupStepsModel(group, "topic-step2", ideas, locale);
        // Specific for step 2
        model.addObject("proposition", new Proposition());
        if (!ideas.isEmpty()) {
            Integer ideaId = ideas.get(0).getId();
            model.addObject("benefits", propositionService.findBenefits(ideaId));
            //model.addObject("pros", propositionService.findPros(ideaId));
            model.addObject("contras", propositionService.findContras(ideaId));
            model.addObject("others", propositionService.findOthers(ideaId));
        } else {
            // TODO Error, contact moderator, there are no ideas
        }
        return model;
    }

    /**
     * Display group step 3
     * @param locale locale
     * @param groupId group id
     * @return step 3 mav
     */
    @RequestMapping(value = {"/topic/{group_id}/step3", "/topic.html/{group_id}/step3"}, method = RequestMethod.GET)
    public ModelAndView showTopicStep3(Locale locale,
                                       @PathVariable(value = "group_id") Integer groupId
    ) {
        List bloc1, bloc2, bloc3, bloc4, bloc5, bloc6, bloc7, bloc8, bloc9;
        Group group = groupService.findById(groupId);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        // Step not reached yet
        if (group.getStatus().compareTo(GroupStatusEnum.CODESIGN) < 0)
            return showTopic(locale, groupId);
        List<Post> ideas = postService.findGroupBestIdea(groupId);
        ModelAndView model = createGroupStepsModel(group, "topic-step3", ideas, locale);
        // Specific for step 3
        model.addObject("newPost", new Post());
        List<Post> posts = postService.findPostsByGroupIdAndPostPhase(group.getId(), PostPhaseEnum.CODESIGN);
        for (Post p: posts)
            p.setDocuments(documentService.findAllByPost(p));
        model.addObject("posts", posts);
        model.addObject("posts", posts);
        if (questionService.groupHasQuestions(group)) { // if exists in database
            bloc1 = retrieveBloc(group, QuestionEnum.BLOC_1);
            bloc2 = retrieveBloc(group, QuestionEnum.BLOC_2);
            bloc3 = retrieveBloc(group, QuestionEnum.BLOC_3);
            bloc4 = retrieveBloc(group, QuestionEnum.BLOC_4);
            bloc5 = retrieveBloc(group, QuestionEnum.BLOC_5);
            bloc6 = retrieveBloc(group, QuestionEnum.BLOC_6);
            bloc7 = retrieveBloc(group, QuestionEnum.BLOC_7);
            bloc8 = retrieveBloc(group, QuestionEnum.BLOC_8);
            bloc9 = retrieveBloc(group, QuestionEnum.BLOC_9);
        } else { // if not exists crete them
            bloc1 = initBloc1(group);
            bloc2 = initBloc2(group);
            bloc3 = initBloc3(group);
            bloc4 = initBloc4(group);
            bloc5 = initBloc5(group);
            bloc6 = initBloc6(group);
            bloc7 = initBloc7(group);
            bloc8 = initBloc8(group);
            bloc9 = initBloc9(group);
        }
        model.addObject("bloc1", bloc1);
        model.addObject("bloc2", bloc2);
        model.addObject("bloc3", bloc3);
        model.addObject("bloc4", bloc4);
        model.addObject("bloc5", bloc5);
        model.addObject("bloc6", bloc6);
        model.addObject("bloc7", bloc7);
        model.addObject("bloc8", bloc8);
        model.addObject("bloc9", bloc9);
        return model;
    }

    private List<QuestionAndAnswers> retrieveBloc(Group group, QuestionEnum type) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        List<Question> questions = questionService.findByGroupAndType(group, type);
        List<Answer> answers;
        for (Question q: questions) {
            answers = answerService.findByQuestion(q);
            qas.add(new QuestionAndAnswers(q, answers));
        }
        return qas;
    }

    private List<QuestionAndAnswers> initBloc1(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_1,
                "topic.step3.bloc1.question1",
                "topic.step3.bloc1.question1.answer1",
                "topic.step3.bloc1.question1.answer2",
                "topic.step3.bloc1.question1.answer3",
                "topic.step3.bloc1.question1.answer4"
                //"topic.step3.bloc1.question1.answer5",
                //"topic.step3.bloc1.question1.answer6"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_1,
                "topic.step3.bloc1.question2",
                "topic.step3.bloc1.question2.answer1",
                "topic.step3.bloc1.question2.answer2",
                "topic.step3.bloc1.question2.answer3",
                "topic.step3.bloc1.question2.answer4",
                "topic.step3.bloc1.question2.answer5",
                "topic.step3.bloc1.question2.answer6",
                "topic.step3.bloc1.question2.answer7"
        ));
        /*qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_1,
                "topic.step3.bloc1.question3",
                "topic.step3.bloc1.question3.answer1",
                "topic.step3.bloc1.question3.answer2",
                "topic.step3.bloc1.question3.answer3",
                "topic.step3.bloc1.question3.answer4",
                "topic.step3.bloc1.question3.answer5",
                "topic.step3.bloc1.question3.answer6",
                "topic.step3.bloc1.question3.answer7",
                "topic.step3.bloc1.question3.answer8",
                "topic.step3.bloc1.question3.answer9",
                "topic.step3.bloc1.question3.answer10",
                "topic.step3.bloc1.question3.answer11",
                "topic.step3.bloc1.question3.answer12",
                "topic.step3.bloc1.question3.answer13"
        ));*/
        return qas;
    }

    private List<QuestionAndAnswers> initBloc2(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_2,
                "topic.step3.bloc2.question1",
                "topic.step3.bloc2.question1.answer1",
                "topic.step3.bloc2.question1.answer2",
                "topic.step3.bloc2.question1.answer3",
                "topic.step3.bloc2.question1.answer4",
                "topic.step3.bloc2.question1.answer5"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_2,
                "topic.step3.bloc2.question2",
                "topic.step3.bloc2.question2.answer1",
                "topic.step3.bloc2.question2.answer2",
                "topic.step3.bloc2.question2.answer3",
                "topic.step3.bloc2.question2.answer4"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_2,
                "topic.step3.bloc2.question3",
                "topic.step3.bloc2.question3.answer1",
                "topic.step3.bloc2.question3.answer2",
                "topic.step3.bloc2.question3.answer3",
                "topic.step3.bloc2.question3.answer4",
                "topic.step3.bloc2.question3.answer5",
                "topic.step3.bloc2.question3.answer6",
                "topic.step3.bloc2.question3.answer7",
                "topic.step3.bloc2.question3.answer8",
                "topic.step3.bloc2.question3.answer9",
                "topic.step3.bloc2.question3.answer10",
                "topic.step3.bloc2.question3.answer11"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_2,
                "topic.step3.bloc2.question4",
                "topic.step3.bloc2.question4.answer1",
                "topic.step3.bloc2.question4.answer2",
                "topic.step3.bloc2.question4.answer3",
                "topic.step3.bloc2.question4.answer4",
                "topic.step3.bloc2.question4.answer5",
                "topic.step3.bloc2.question4.answer6",
                "topic.step3.bloc2.question4.answer7",
                "topic.step3.bloc2.question4.answer8",
                "topic.step3.bloc2.question4.answer9"
        ));
        return qas;
    }

    private List<QuestionAndAnswers> initBloc3(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_3,
                "topic.step3.bloc3.question1",
                "topic.step3.bloc3.question1.answer1",
                "topic.step3.bloc3.question1.answer2",
                "topic.step3.bloc3.question1.answer3",
                "topic.step3.bloc3.question1.answer4"
                //"topic.step3.bloc3.question1.answer5",
                //"topic.step3.bloc3.question1.answer6",
                //"topic.step3.bloc3.question1.answer7",
                //"topic.step3.bloc3.question1.answer8",
                //"topic.step3.bloc3.question1.answer9",
                //"topic.step3.bloc3.question1.answer10",
                //"topic.step3.bloc3.question1.answer11"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_3,
                "topic.step3.bloc3.question2",
                "topic.step3.bloc3.question2.answer1",
                "topic.step3.bloc3.question2.answer2",
                "topic.step3.bloc3.question2.answer3",
                "topic.step3.bloc3.question2.answer4",
                "topic.step3.bloc3.question2.answer5"
        ));
        /*qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_3,
                "topic.step3.bloc3.question3",
                "topic.step3.bloc3.question3.answer1",
                "topic.step3.bloc3.question3.answer2",
                "topic.step3.bloc3.question3.answer3",
                "topic.step3.bloc3.question3.answer4",
                "topic.step3.bloc3.question3.answer5",
                "topic.step3.bloc3.question3.answer6",
                "topic.step3.bloc3.question3.answer7",
                "topic.step3.bloc3.question3.answer8",
                "topic.step3.bloc3.question3.answer9"
        ));*/
        return qas;
    }

    private List<QuestionAndAnswers> initBloc4(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_4,
                "topic.step3.bloc4.question1",
                "topic.step3.bloc4.question1.answer1",
                "topic.step3.bloc4.question1.answer2",
                "topic.step3.bloc4.question1.answer3"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_4,
                "topic.step3.bloc4.question2",
                "topic.step3.bloc4.question2.answer1",
                "topic.step3.bloc4.question2.answer2",
                "topic.step3.bloc4.question2.answer3",
                "topic.step3.bloc4.question2.answer4",
                "topic.step3.bloc4.question2.answer5",
                "topic.step3.bloc4.question2.answer6",
                "topic.step3.bloc4.question2.answer7"
        ));
        /*qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_4,
                "topic.step3.bloc4.question3",
                "topic.step3.bloc4.question3.answer1",
                "topic.step3.bloc4.question3.answer2",
                "topic.step3.bloc4.question3.answer3"
        ));*/
        return qas;
    }

    private List<QuestionAndAnswers> initBloc5(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_5,
                "topic.step3.bloc5.question1",
                "topic.step3.bloc5.question1.answer1",
                "topic.step3.bloc5.question1.answer2",
                "topic.step3.bloc5.question1.answer3",
                "topic.step3.bloc5.question1.answer4"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_5,
                "topic.step3.bloc5.question2",
                "topic.step3.bloc5.question2.answer1",
                "topic.step3.bloc5.question2.answer2",
                "topic.step3.bloc5.question2.answer3",
                "topic.step3.bloc5.question2.answer4",
                "topic.step3.bloc5.question2.answer5"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_5,
                "topic.step3.bloc5.question3",
                "topic.step3.bloc5.question3.answer1",
                "topic.step3.bloc5.question3.answer2",
                "topic.step3.bloc5.question3.answer3"
        ));
        return qas;
    }

    private List<QuestionAndAnswers> initBloc6(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        /*qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_6,
                "topic.step3.bloc6.question1",
                "topic.step3.bloc6.question1.answer1",
                "topic.step3.bloc6.question1.answer2",
                "topic.step3.bloc6.question1.answer3"
        ));*/
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_6,
                "topic.step3.bloc6.question2",
                "topic.step3.bloc6.question2.answer1",
                "topic.step3.bloc6.question2.answer2",
                "topic.step3.bloc6.question2.answer3",
                "topic.step3.bloc6.question2.answer4",
                "topic.step3.bloc6.question2.answer5",
                "topic.step3.bloc6.question2.answer6",
                "topic.step3.bloc6.question2.answer7",
                "topic.step3.bloc6.question2.answer8",
                "topic.step3.bloc6.question2.answer9",
                "topic.step3.bloc6.question2.answer10",
                "topic.step3.bloc6.question2.answer11"
        ));
        return qas;
    }

    private List<QuestionAndAnswers> initBloc7(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_7,
                "topic.step3.bloc7.question1",
                "topic.step3.bloc7.question1.answer1",
                "topic.step3.bloc7.question1.answer2",
                "topic.step3.bloc7.question1.answer3",
                "topic.step3.bloc7.question1.answer4"
                //"topic.step3.bloc7.question1.answer5",
                //"topic.step3.bloc7.question1.answer6",
                //"topic.step3.bloc7.question1.answer7"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_7,
                "topic.step3.bloc7.question2",
                "topic.step3.bloc7.question2.answer1",
                "topic.step3.bloc7.question2.answer2",
                "topic.step3.bloc7.question2.answer3",
                "topic.step3.bloc7.question2.answer4",
                "topic.step3.bloc7.question2.answer5",
                "topic.step3.bloc7.question2.answer6"
        ));
        return qas;
    }

    private List<QuestionAndAnswers> initBloc8(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_8,
                "topic.step3.bloc8.question1",
                "topic.step3.bloc8.question1.answer1",
                "topic.step3.bloc8.question1.answer2",
                "topic.step3.bloc8.question1.answer3",
                "topic.step3.bloc8.question1.answer4",
                "topic.step3.bloc8.question1.answer5",
                "topic.step3.bloc8.question1.answer6",
                "topic.step3.bloc8.question1.answer7",
                "topic.step3.bloc8.question1.answer8"
        ));
        return qas;
    }

    private List<QuestionAndAnswers> initBloc9(Group group) {
        List<QuestionAndAnswers> qas = new LinkedList<>();
        Locale locale = new Locale(group.getLanguage().getCode());
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_9,
                "topic.step3.bloc9.question1",
                //"topic.step3.bloc9.question1.answer1",
                "topic.step3.bloc9.question1.answer2",
                "topic.step3.bloc9.question1.answer3",
                "topic.step3.bloc9.question1.answer4",
                "topic.step3.bloc9.question1.answer5",
                "topic.step3.bloc9.question1.answer6",
                "topic.step3.bloc9.question1.answer7"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_9,
                "topic.step3.bloc9.question2",
                "topic.step3.bloc9.question2.answer1",
                "topic.step3.bloc9.question2.answer2",
                "topic.step3.bloc9.question2.answer3",
                "topic.step3.bloc9.question2.answer4",
                "topic.step3.bloc9.question2.answer5",
                "topic.step3.bloc9.question2.answer6",
                "topic.step3.bloc9.question2.answer7"
        ));
        qas.add(createQuestionAndAnswers(group, QuestionEnum.BLOC_9,
                "topic.step3.bloc9.question3",
                "topic.step3.bloc9.question3.answer1",
                "topic.step3.bloc9.question3.answer2",
                "topic.step3.bloc9.question3.answer3"
        ));
        return qas;
    }

    private QuestionAndAnswers createQuestionAndAnswers(Group group, QuestionEnum bloc, String questionText, String... answerTexts) {
        Answer answer;
        Question question = questionService.save(new Question(questionText, bloc, group));
        List<Answer> answers = new LinkedList<>();
        for (String answerText : answerTexts) {
            answer = answerService.save(new Answer(answerText, question, 0));
            answers.add(answer);
        }
        return new QuestionAndAnswers(question, answers);
    }

    /**
     * Display group step 4
     * @param locale locale
     * @param groupId group id
     * @return step 4 mav
     */
    @RequestMapping(value = {"/topic/{group_id}/step4", "/topic.html/{group_id}/step4"}, method = RequestMethod.GET)
    public ModelAndView showTopicStep4(Locale locale,
                                       @PathVariable(value = "group_id") Integer groupId
                                       ) {
        List<Post> posts;
        Group group = groupService.findById(groupId);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        // Step not reached yet
        if (group.getStatus().compareTo(GroupStatusEnum.TEST) < 0)
            return showTopic(locale, groupId);
        List<Post> ideas = postService.findGroupBestIdea(groupId);
        ModelAndView model = createGroupStepsModel(group, "topic-step4", ideas, locale);
        // Specific for step 4
        Organisation organisation = ideas.get(0).getOrganisation();
        if (organisation == null) {
            // NO ORG
            Optional<User> user;
            try {
                user = Optional.of(Methods.getLoggedInUser(userService));
            } catch (ClassCastException e) {
                user = Optional.empty();
            }
            // For all users
            posts = postService.findPostsByGroupIdAndPostPhase(groupId, PostPhaseEnum.ORG_APPLY);
            for (Post p: posts)
                p.setDocuments(documentService.findAllByPost(p));
            model.addObject("posts", posts);
            // Specific for SP
            if(user.isPresent() && user.get().getOrganisation() != null) {
                model.addObject("can_apply", postService.hasOrganisationNotAppliedToIdea(group, user.get().getOrganisation()));
                model.addObject("newPost", new Post());
            }
        } else {
            // YES ORG
            model.addObject("feedback", new Feedback());
            posts = postService.findPostsByGroupIdAndPostPhase(groupId, PostPhaseEnum.TEST);
            for (Post p: posts)
                p.setDocuments(documentService.findAllByPost(p));
            model.addObject("posts", posts);
            model.addObject("newPost", new Post());
            if (!ideas.isEmpty()) {
                model.addObject("organisation", organisation);
                List<Feedback> feedbacks = feedbackService.findFeedbackByIdeaId(ideas.get(0).getId());
                List<FeedbackWithContributions> feedbacksWithContributions = new LinkedList<>();
                //List<FeedbackWithContributions> archivesWithContributions = new LinkedList<>();
                FeedbackWithContributions fc;
                List<Answer> answers;
                List<Document> documents;
                int upvotes, downvotes;
                // Set feedbacks
                for (Feedback f : feedbacks) {
                    // Get all documents of each feedback
                    documents = documentService.findAllByFeedback(f);
                    // Is Organisation request
                    if (f.isByOrg()) {
                        posts = postService.findByFeedbackAndVisible(f, VisibleEnum.VISIBLE);
                        for (Post p: posts)
                            p.setDocuments(documentService.findAllByPost(p));
                        fc = new FeedbackWithContributions(f, posts, documents);
                    }
                    // Is User vote
                    else {
                        answers = answerService.findByFeedback(f);
                        if (answers == null || answers.isEmpty()) {
                            try {
                                upvotes = userFeedbackVoteRepository.countByFeedbackAndVoteType(f, VoteTypeEnum.UP_VOTED);
                            } catch (Exception e) {
                                upvotes = 0;
                            }
                            try {
                                downvotes = userFeedbackVoteRepository.countByFeedbackAndVoteType(f, VoteTypeEnum.DOWN_VOTED);
                            } catch (Exception e) {
                                downvotes = 0;
                            }
                            fc = new FeedbackWithContributions(f, upvotes, downvotes, documents);
                        } else
                            fc = new FeedbackWithContributions(answers, f, documents);
                    }
                    feedbacksWithContributions.add(fc);
                }
                model.addObject("feedbacks", feedbacksWithContributions);
            } else {
                // TODO Error, contact moderator, there are no ideas
            }
        }
        return model;
    }

    /**
     * Display group step 5
     * @param locale locale
     * @param groupId group id
     * @return step 5 mav
     */
    @RequestMapping(value = {"/topic/{group_id}/step5", "/topic.html/{group_id}/step5"}, method = RequestMethod.GET)
    public ModelAndView showTopicStep5(Locale locale,
                                       @PathVariable(value = "group_id") Integer groupId
    ) {
        Group group = groupService.findById(groupId);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        // Step not reached yet
        if (group.getStatus().compareTo(GroupStatusEnum.COMPLETED) < 0)
            return showTopic(locale, groupId);
        List<Post> ideas = postService.findGroupBestIdea(groupId);
        ModelAndView model = createGroupStepsModel(group, "topic-step5", ideas, locale);
        // Specific for step 5
        List<Post> posts = postService.findPostsByGroupIdAndPostPhase(groupId, PostPhaseEnum.COMPLETED);
        for (Post p: posts)
            p.setDocuments(documentService.findAllByPost(p));
        Service service = serviceService.findByGroup(group);
        if (service != null)
            service.setDocuments(documentService.findAllByService(service));
        model.addObject("posts", posts);
        model.addObject("newPost", new Post());
        model.addObject("newService", new Service());
        model.addObject("service", serviceService.findByGroup(group));
        model.addObject("organisation", ideas.get(0).getOrganisation());
        return model;
    }

    @RequestMapping(value = {"/topic/{group_id}/kb_data"}, method = RequestMethod.GET)
    public ModelAndView showTopicKbData(Locale locale,
                                        @PathVariable(value = "group_id") Integer groupId) {
        Group group = groupService.findById(groupId);
        int localityId;
        try {
            localityId = Methods.getLoggedInUser(userService).getLocality().getId();
        } catch (Exception e) {
            localityId = group.getLocality().getId();
        }
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        List<Post> ideas = postService.findGroupBestIdea(groupId);
        ModelAndView model = createGroupStepsModel(group, "topic-kb", ideas, locale);
        Service service = serviceService.findByGroup(group);
        model.addObject("service", service);
        if (service != null) {
            ResponseEntity<List<SimilarService>> similarServices2Service =
                    restAPICaller.searchSimilarServicesToService(service.getId());
            model.addObject("similarServices2Service", restAPICaller.retrieveServices(service.getId(), false));
        }
        model.addObject("group", group);
        model.addObject("tweets", restAPICaller.retrieveTweets(group.getThemes(), group.getLanguage().getCode()));
        model.addObject("similarServices2Group", restAPICaller.retrieveServices(group.getId(), true));
        model.addObject("similarGroups", restAPICaller.retrieveGroupsToGroup(groupId, localityId));
        model.addObject("activeUsers", retrieveUsers(restAPICaller.searchMostActiveUsersInGroup(group.getId())));
        return model;
    }

    /**
     * Create group model and view
     * @param group group
     * @param viewName view name
     * @param ideas ideas
     * @param locale locale
     * @return group mav
     */
    private ModelAndView createGroupStepsModel(Group group, String viewName, List<Post> ideas, Locale locale) {

        ModelAndView model;
        Optional<User> optUser = Optional.empty();
        String imageName = "STEPBYSTEP_";
        Resource resource;

        try {
            User user = Methods.getLoggedInUser(userService);
            optUser = Optional.of(user);
            // Forbidden group for this user
            if (!userCanAccessGroup(user, group))
                throw new GroupNotAccessibleException(group.getName() + " (ID=" + group.getId() + ")");
            // If the first user (besides initiator) enters to a topic, the process starts.
            // It has 1 month
            if (group.getNextStepTimestamp() == null && !user.equals(group.getInitiator())) { // TODO && user.getRole().equals(UserRoleEnum.USER)
                Timestamp timestamp = Methods.oneMonthFromNow();
                groupScheduler.createNewTask(group.getId(), timestamp);
                group.setNextStepTimestamp(timestamp);
                groupService.save(group);
                logger.debug("Group started " + group.getId() + " " + group.getName());
            }
            historyService.createHistory(user, null, group, "history.viewed_group", HistoryTypeEnum.VIEWED_GROUP, VisibleEnum.VISIBLE);
            logger.debug("User " + user.getUsername() + " views group " + group.getId()  + " " + group.getName());
            imageName = imageName + user.getFirstLang().getCode() + ".jpg";
            resource = new ClassPathResource("static/images/" + imageName);
        } catch (ClassCastException e) {
            if (!locale.getLanguage().equals(group.getLanguage().getCode()))
                throw new GroupNotAccessibleException(group.getName() + " (ID=" + group.getId() + ")");
            imageName = imageName + locale.getLanguage() + ".jpg";
            resource = new ClassPathResource("static/images/" + imageName);
            logger.debug("Anonymous user views group " + group.getId()  + " " + group.getName());
        }

        model = createGroupModel(optUser, viewName, locale);
        if (optUser.isPresent()) {
            model.addObject("user", optUser.get());
            model.addObject("relation", groupService.findGroupRelation(group, optUser.get()));
        }
        if (resource.exists())
            model.addObject("image_name", imageName);
        else model.addObject("image_name", "STEPBYSTEP_en.jpg");
        model.addObject("report", new Report());
        model.addObject("timeRemaining", methods.timeRemaining(group, locale));
        model.addObject("group", group);
        model.addObject("ideas", ideas);
        model.addObject("contributors", groupService.getContributors(group.getId()));
        model.addObject("numParticipants", groupService.countContributors(group.getId()));
        model.addObject("numSubscribers", groupService.countSubscribers(group.getId()));
        model.addObject("numOlderPeople", groupService.countOlderPeople(group.getId()));
        model.addObject("olderPeople", groupService.getOlderPeople(group.getId()));
        model.addObject("numFamilyFriends", groupService.countFamilyFriends(group.getId()));
        model.addObject("familyFriends", groupService.getFamilyFriends(group.getId()));
        model.addObject("numCareAssistants", groupService.countCareAssistants(group.getId()));
        model.addObject("careAssistants", groupService.getCareAssistants(group.getId()));
        model.addObject("numHealthcareSpecialist", groupService.countHealthcareSpecialists(group.getId()));
        model.addObject("healthcareSpecialist", groupService.getHealthcareSpecialists(group.getId()));
        model.addObject("numServiceProviders", groupService.countServiceProviders(group.getId()));
        model.addObject("serviceProviders", groupService.getServiceProviders(group.getId()));
        model.addObject("numPolicyMakers", groupService.countPolicyMakers(group.getId()));
        model.addObject("policyMakers", groupService.getPolicyMakers(group.getId()));
        model.addObject("numOthers", groupService.countOthers(group.getId()));
        model.addObject("otherProfiles", groupService.getOthers(group.getId()));
        model.addObject("documents", documentService.findAllByGroup(group));
        return model;
    }

    private List<User> retrieveUsers(ResponseEntity<List<UserActivity>> responseEntity) {
        if (responseEntity == null) return new LinkedList<>();
        List body = responseEntity.getBody();
        if (body == null || body.isEmpty()) return new LinkedList<>();
        List<UserActivity> userActivities = mapper.convertValue(body, new TypeReference<List<UserActivity>>(){});
        List<Integer> userIds = userActivities.stream().map(UserActivity::getUser_id).map(Methods::decrypt).collect(Collectors.toList());
        return userRepository.findAllById(userIds);
    }

    /**
     * Organisation applies to an idea
     * @param locale locale
     * @param groupId group id
     * @return display step 4
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = {"/topic/{group_id}/apply"}, method = RequestMethod.POST)
    public ModelAndView applyToIdea(Locale locale,
                              @Valid Post post,
                              @PathVariable(value = "group_id") Integer groupId,
                              @RequestParam("file") Optional<MultipartFile[]> files
    ) {
        Group group = groupService.findById(groupId);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        // Step not reached yet
        if (group.getStatus().compareTo(GroupStatusEnum.TEST) < 0)
            return showTopic(locale, groupId);
        User user = Methods.getLoggedInUser(userService);
        if (user.getOrganisation() == null) return new ModelAndView("errors/error", "error", messageSource.getMessage("topic.error.only_service_provider", null, new Locale(user.getFirstLang().getCode())));
        Post idea = postService.findGroupBestIdea(groupId).get(0);
        if (idea.getOrganisation() != null) return new ModelAndView("errors/error", "error", messageSource.getMessage("topic.error.idea_applied", null, new Locale(user.getFirstLang().getCode())));
        if (postService.hasOrganisationNotAppliedToIdea(group, user.getOrganisation())) {
            post.setPostPhase(PostPhaseEnum.ORG_APPLY);
            post.setVisible(VisibleEnum.PENDING);
            post.setAuthor(user);
            post.setGroup(group);
            post.setTimestamp(new Timestamp(System.currentTimeMillis()));
            Post newPost = postService.save(post);
            if (files.isPresent()) {
                for (MultipartFile file: files.get())
                    if (!file.isEmpty())
                        documentService.storeFile(file, null, newPost.getId(), null, null, null, null, new Locale(user.getFirstLang().getCode()));
            }
        } else {
            return new ModelAndView("errors/error", "error", messageSource.getMessage("topic.error.already_applied", null, new Locale(user.getFirstLang().getCode())));
        }
        // Make organisation members contributors
        for (User u : userService.getOrganisationMembers(user.getOrganisation()))
            makeUserContributor(u, group);
        return new ModelAndView("redirect:/topic/" + groupId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = {"/topic/{group_id}/create_service"})
    public String createGroupService(@RequestParam("file") Optional<MultipartFile[]> files,
                                     @RequestParam("service_logo") Optional<MultipartFile> logo,
                                     @Valid Service service,
                                     @PathVariable(value = "group_id") Integer groupId) {
        Group group = groupService.findById(groupId);
        User user = Methods.getLoggedInUser(userService);
        // Unexisting group
        if (group == null) throw new GroupNotFoundException(String.valueOf(groupId));
        // Step not reached yet
        if (group.getStatus().compareTo(GroupStatusEnum.COMPLETED) < 0)
            return "redirect:/topic/" + groupId;
        // if service exists -> update
        Service s = serviceService.findByGroup(group);
        if (s != null) {
            service.setId(s.getId());
            Document prevLogo = s.getLogo();
            if (logo.isPresent() && !logo.get().isEmpty()) {
                Document docLogo = documentService.storeFile(logo.get(), null, null, null, null, null, null, new Locale(user.getFirstLang().getCode()));
                service.setLogo(docLogo);
            } else {
                service.setLogo(prevLogo);
            }
        } else {
            notificationService.notifyAllParticipantsInGroup(groupId, "notification.participant.service_created", "/topic/" + groupId, group.getName());
        }
        // if lang = 'en' -> nativeDescription = description
        if (group.getLanguage().getCode().equalsIgnoreCase("en")) {
            service.setDescription(service.getNativeDescription());
        }

        Post idea = postService.findGroupBestIdea(groupId).get(0);

        s = serviceService.registerService(service, idea.getOrganisation(), group, themeRepository.findGroupThemes(groupId));

        if (files.isPresent()) {
            for (MultipartFile file : files.get())
                if (!file.isEmpty())
                    documentService.storeFile(file, null, null, null, null, null, s.getId(), new Locale(user.getFirstLang().getCode()));
        }
        return "redirect:/topic/" + groupId;
    }

    /**
     * Make user contributor of the group
     * @param user user
     * @param group group
     */
    private void makeUserContributor(User user, Group group) {
        GroupUserRelation relation = groupService.findGroupRelation(group, user);
        if (relation == null)
            groupService.createGroupUserRelation(group, user, GroupUserRelationEnum.CONTRIBUTOR);
        else if (relation.getRelation().equals(GroupUserRelationEnum.SUBSCRIBED))
            groupService.changeRelationWithUser(group, user, GroupUserRelationEnum.CONTRIBUTOR);
    }

    /**
     * Retrieve future with the group's related services
     * @param relatedServicesFuture future
     * @return related services to topics
     */
    private List<ServiceByTopic> retrieveRelatedServices(CompletableFuture<ResponseEntity<ServicesList>> relatedServicesFuture) {
        try {
            ResponseEntity<ServicesList> response = relatedServicesFuture.get();
            ServicesList servicesList = response.getBody();
            if (response.getStatusCode().value() == 200 &&
                    servicesList != null &&
                    servicesList.getServicesByTopics() != null &&
                    !servicesList.getServicesByTopics().isEmpty()) {
                return servicesList.getServicesByTopics();
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            logger.error("Knowledge Base error", new KnowledgeBaseException(e));
        }
        return new LinkedList<>();
    }

    /**
     * Check if user can access a group
     * @param user user
     * @param group group
     * @return can access?
     */
    private boolean userCanAccessGroup(User user, Group group) {
        // If locality is PanEuropean and Language is English (group_language)
        if (group.getLocality().getId() == 999
                &&
                (user.getFirstLang().equals(group.getLanguage())
                        ||
                (user.getSecondLang() != null && user.getSecondLang().equals(group.getLanguage()))))
            return true;
        // If user_locality = group_locality and user_firstLang/user_secondLang = group_lang
        return (user.getLocality().equals(group.getLocality())
                        ||
                user.getParentLocality().equals(group.getLocality()))
                &&
                (user.getFirstLang().equals(group.getLanguage())
                        ||
                (user.getSecondLang() != null && user.getSecondLang().equals(group.getLanguage())));
    }

    /**
     * Unsubscribe group
     * @param groupId group id
     * @return number of subscribers
     */
    @RequestMapping("/topic/{group_id}/unsubscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity unsubscribe(@PathVariable(value = "group_id") Integer groupId) {
        User user = Methods.getLoggedInUser(userService);
        Group group = groupService.findById(groupId);
        if (group == null)
            throw new GroupNotFoundException(String.valueOf(groupId));
        GroupUserRelation relation = groupService.findGroupRelation(group, user);
        if (relation == null)
            return ResponseEntity.badRequest().build();
        if (!relation.getRelation().equals(GroupUserRelationEnum.SUBSCRIBED))
            return ResponseEntity.badRequest().build();
        groupService.deleteGroupUserRelation(relation);
        historyService.createHistory(user, null, group, "history.unsubscribed_group", HistoryTypeEnum.UNSUBSCRIBED_GROUP, VisibleEnum.VISIBLE);
        logger.debug("User " + user.getUsername() + " unsubscribed group " + groupId);
        return ResponseEntity.ok(new HashMap<String, Long>() {{
            put("subscribed", 0L);
            put("num", groupService.countSubscribers(groupId));
        }});
    }

    /**
     * Subscribe group
     * @param groupId group id
     * @return number of subscribers
     */
    @RequestMapping(value = "/topic/{group_id}/subscribe", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity subscribe(@PathVariable(value = "group_id") Integer groupId) {
        User user = Methods.getLoggedInUser(userService);
        Group group = groupService.findById(groupId);
        if (group == null)
            throw new GroupNotFoundException(String.valueOf(groupId));
        if (groupService.findGroupRelation(group, user) != null)
            return ResponseEntity.badRequest().build();
        groupService.createGroupUserRelation(group, user, GroupUserRelationEnum.SUBSCRIBED);
        historyService.createHistory(user, null, group, "history.subscribed_group", HistoryTypeEnum.SUBSCRIBED_GROUP, VisibleEnum.VISIBLE);
        logger.debug("User " + user.getUsername() + " subscribed group " + groupId);
        return ResponseEntity.ok(new HashMap<String, Long>() {{
            put("subscribed", 1L);
            put("num", groupService.countSubscribers(groupId));
        }});
    }

    @RequestMapping(value = "/topic/{group_id}/summary/{num}", method = RequestMethod.POST)
    public String updateSummary(@RequestParam String summary, @PathVariable("group_id") Integer groupId, @PathVariable("num") Integer num) {
        Group group = groupService.findById(groupId);
        if (group == null)
            throw new GroupNotFoundException(String.valueOf(groupId));
        switch (num) {
            case 2:
                group.setStep1summary(summary);
                break;
            case 3:
                group.setStep2summary(summary);
                break;
            case 4:
                group.setStep3summary(summary);
                break;
            case 5:
                group.setStep4summary(summary);
                break;
        }
        groupService.save(group);
        return "redirect:/topic/" + groupId;
    }

    /**
     * Check if there are new posts
     * @param groupId group id
     * @param date current date
     * @return number of new posts after date
     */
    @RequestMapping(value = "/topic/{group_id}/are-new-posts")
    public @ResponseBody Long areThereNewPosts(@PathVariable(value = "group_id") Integer groupId,
                                               @RequestParam("date") @DateTimeFormat(pattern="dd/MM/yyyy - HH:mm:ss") Date date) {
        return postService.countByGroupIdAndTimestampAfter(groupId, new Timestamp(date.getTime()));
    }

    @RequestMapping(value = "/topic/{group_id}/invite/{participant}")
    public ResponseEntity inviteParticipantByUsername(@PathVariable(value = "group_id") Integer groupId,
                                            @PathVariable(value = "participant") String participant) {
        Group group = groupService.findById(groupId);
        User user = userService.findByUsername(participant);
        if (user != null) {
            notificationService.notifyUser(user, "notification.topic.invite_participant", "/topic/" + group.getId(), group.getName());
            return ResponseEntity.ok().build();
        } else {
            // user doesn't exist
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/topic/{group_id}/invite")
    public ResponseEntity inviteParticipantByEmail(@PathVariable(value = "group_id") Integer groupId,
                                         @ModelAttribute("participant") String participant,
                                         HttpServletRequest request,
                                         Locale locale) {
        Group group = groupService.findById(groupId);
        User user = Methods.getLoggedInUser(userService);
        // If email
        if (participant.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\" +
                "x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\" +
                "[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
                "[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-" +
                "\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            String url = request.getRequestURL().toString().replaceAll("([0-9]+)/invite", "");
            url = url + groupId;
            String subject = messageSource.getMessage("email.topic.invite_participant.subject", null, locale);
            String body = messageSource.getMessage("email.topic.invite_participant.body", new Object[] {group.getName(), user.getUsername(), url}, locale);
            emailService.sendEmail(participant, subject, body, locale);
            return ResponseEntity.ok().build();
        } else { // if not email, maybe is a username
            return inviteParticipantByUsername(groupId, participant);
        }
    }

    @RequestMapping(value = "/topic/{group_id}/contact")
    public ResponseEntity contactParticipantByEmail(@PathVariable(value = "group_id") Integer groupId,
                                                    @ModelAttribute("subject") String subject,
                                                   @ModelAttribute("message") String message,
                                                   HttpServletRequest request,
                                                   Locale locale) {
        try {
            User user = Methods.getLoggedInUser(userService);
            if (subject == null || message == null ||
                    subject.trim().isEmpty() || message.trim().isEmpty() ||
                    subject.trim().equals("") || message.trim().equals(""))
                return ResponseEntity.badRequest().build();
            logger.debug("Contact participant by " + user.getId() + ". subject: " + subject + "\nmessage: " + message);
            groupService.findUsersByGroupId(groupId).forEach(participant -> emailService.sendEmail(participant.getEmail(), subject, message, locale));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Find username to chat
     * @param searchString string to search like username
     * @return return list of  existing usernames
     */
    @RequestMapping(value = "/topic/{group_id}/username/search")
    public @ResponseBody List<String> findUsernameLike(@PathVariable(value = "group_id") Integer groupId,
                                                       @RequestParam("searchedString") String searchString) {
        User user = Methods.getLoggedInUser(userService);
        // TODO change to invite people who is not already a participant
        return (List<String>) userService.findByUsernameLike("%" + searchString + "%", user.getUsername());
    }

}
