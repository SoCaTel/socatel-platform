package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.models.*;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.models.relationships.UserPostVote;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.UserPostVoteRepository;
import com.socatel.services.document.DocumentService;
import com.socatel.services.feedback.FeedbackService;
import com.socatel.services.group.GroupService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.report.ReportService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@Controller
public class PostController {

    @Autowired private GroupService groupService;
    @Autowired private PostService postService;
    @Autowired private UserService userService;
    @Autowired private UserPostVoteRepository userPostVoteRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private ReportService reportService;
    @Autowired private ElasticsearchPublisher elasticsearchPublisher;
    @Autowired private DocumentService documentService;
    @Autowired private FeedbackService feedbackService;

    private Logger logger = LoggerFactory.getLogger(PostController.class);

    /**
     * Create a new post (step 1)
     * @param post new post
     * @param groupId group id
     * @param parentId post parent id
     * @param request servlet request
     * @param files files
     * @param requestM multipart request
     * @return redirect to group
     */
    @RequestMapping(value = {"/topic/{group_id}/post/{parent_id}", "/topic.html/{group_id}/post/{parent_id}"}, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public String post(@Valid Post post, @PathVariable(value = "group_id") Integer groupId,
                       @PathVariable(value = "parent_id") Integer parentId, HttpServletRequest request,
                       @RequestParam("file") Optional<MultipartFile[]> files, MultipartRequest requestM) {
        User user = Methods.getLoggedInUser(userService);
        Group group = groupService.findById(groupId);
        post.setAuthor(user);
        post.setGroup(group);
        // Contributor when contributing
        makeUserContributor(user, group);
        // Set as a reply
        if (parentId != 0) {
            Post postParent = postService.findById(parentId);
            post.setPostParent(postParent);
            notificationService.notifyUser(postParent.getAuthor(), "notification.participant.replied", "/topic/" + groupId, group.getName());
        }
        // Set COMMENT if not IDEA selected
        if (post.getPostType() == null)
            post.setPostType(PostTypeEnum.COMMENT);
        switch (group.getStatus()) {
            case IDEATION:
                post.setPostPhase(PostPhaseEnum.IDEATION);
                if (post.getPostType().equals(PostTypeEnum.IDEA))
                    notificationService.notifyAllParticipantsInGroup(groupId, "notification.participant.idea_posted", "/topic/" + groupId, group.getName());
                break;
            case VALIDATION:
                post.setPostPhase(PostPhaseEnum.VALIDATION);
                break;
            case CODESIGN:
                post.setPostPhase(PostPhaseEnum.CODESIGN);
                break;
            case TEST:
                post.setPostPhase(PostPhaseEnum.TEST);
                break;
            case COMPLETED:
                post.setPostPhase(PostPhaseEnum.COMPLETED);
                break;
            default:
                throw new RuntimeException();
        }
        post.setTimestamp(new Timestamp(System.currentTimeMillis()));
        Post newPost = postService.save(post);
        if (files.isPresent()) {
            for (MultipartFile file: files.get())
                if (!file.isEmpty())
                    documentService.storeFile(file, group.getId(), newPost.getId(), null, null, null, null, new Locale(user.getFirstLang().getCode()));
        }
        logger.debug("Posted new post " + post.getId() + " in group " + groupId);
        return "redirect:/topic/" + groupId + "#tabs"; // TODO Change?
    }

    /**
     * Create a new post (step 4)
     * @param post post
     * @param groupId group id
     * @param feedbackId feedback id
     * @return redirect to group
     */
    @PreAuthorize(value = "isAuthenticated()")
    @RequestMapping(value = {"/topic/{group_id}/feedback/{feedback_id}/propose"}, method = RequestMethod.POST)
    public String postInStep4(@RequestParam("file") Optional<MultipartFile[]> files, @Valid Post post, @PathVariable(value = "group_id") Integer groupId, @PathVariable(value = "feedback_id") Integer feedbackId) {
        User user = Methods.getLoggedInUser(userService);
        Group group = groupService.findById(groupId);
        // Contributor when contributing
        makeUserContributor(user, group);
        Feedback feedback = feedbackService.findFeedbackById(feedbackId);
        post.setGroup(group);
        post.setAuthor(user);
        post.setFeedback(feedback);
        post.setPostPhase(PostPhaseEnum.TEST);
        post.setTimestamp(new Timestamp(System.currentTimeMillis()));
        logger.debug("Posted new proposition " + post.getId() + " in feedback " + feedbackId + " in group " + groupId);
        post = postService.save(post);
        if (files.isPresent()) {
            for (MultipartFile file: files.get())
                if (!file.isEmpty())
                    documentService.storeFile(file, null, post.getId(), null, null, null, null, new Locale(user.getFirstLang().getCode()));
        }
        return "redirect:/topic/" + groupId + "/step4#tabs";
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
     * Change post tag
     * @param postId post id
     * @return redirect to group
     */
    @RequestMapping(value = {"/post/{post_id}/change_tag"})
    @PreAuthorize("isAuthenticated()")
    public String changeTagPost(@PathVariable(value = "post_id") Integer postId) {
        Post post = postService.findById(postId);
        User user =  Methods.getLoggedInUser(userService);
        if (post.canBeModifiedBy(user)) {
            if (post.getPostType().equals(PostTypeEnum.COMMENT))
                post.setPostType(PostTypeEnum.IDEA);
            else
                post.setPostType(PostTypeEnum.COMMENT);
            postService.save(post);
            logger.debug("Changed tag post " + post.getId());
        }
        return "redirect:/topic/" + post.getGroup().getId() + "#tabs";
    }

    /**
     * Delete post
     * @param postId post id
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"/post/{post_id}/delete"})
    @PreAuthorize("isAuthenticated()")
    public void deletePost(@PathVariable(value = "post_id") Integer postId) {
        Post post = postService.findById(postId);
        User user = Methods.getLoggedInUser(userService);
        if (post.canBeModifiedBy(user)) {
            postService.mask(post.getId());
            logger.debug("Deleted post " + post.getId());
        }
    }

    /**
     * Up vote post (idea only)
     * @param postId post id
     * @return OK status with number of upvotes and downvotes
     */
    @RequestMapping(value = "/post/{post_id}/upvote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity upVotePost(@PathVariable(value = "post_id") Integer postId) {
        return ResponseEntity.ok(votePost(true, postId));
    }

    /**
     * Down vote post (idea only)
     * @param postId post id
     * @return OK status with number of upvotes and downvotes
     */
    @RequestMapping(value = "/post/{post_id}/downvote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity downVotePost(@PathVariable(value = "post_id") Integer postId) {
        return ResponseEntity.ok(votePost(false, postId));
    }

    /**
     * Vote up/down a Post
     * @param isUpVote is upvote (true) or downvote (false)
     * @param postId post id
     */
    private HashMap<String, Integer> votePost(boolean isUpVote, Integer postId) {
        User user = Methods.getLoggedInUser(userService);
        Post post = postService.findById(postId);
        Optional<UserPostVote> optionalUserPostVote = userPostVoteRepository.findByUserAndPost(user, post);
        UserPostVote userPostVote = optionalUserPostVote.orElse(new UserPostVote(user, post));
        if (isUpVote) {
            logger.debug("Upvoted post " + postId + " by " + user.getUsername());
            if (userPostVote.getVoteType().equals(VoteTypeEnum.UP_VOTED)) {
                userPostVote.setVoteType(VoteTypeEnum.NO_VOTED);
                post.upVote(false);
            } else {
                if (userPostVote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED))
                    post.downVote(false);
                userPostVote.setVoteType(VoteTypeEnum.UP_VOTED);
                post.upVote(true);
            }
        } else {
            logger.debug("Downvoted post " + postId + " by " + user.getUsername());
            if (userPostVote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED)) {
                userPostVote.setVoteType(VoteTypeEnum.NO_VOTED);
                post.downVote(false);
            } else {
                if (userPostVote.getVoteType().equals(VoteTypeEnum.UP_VOTED))
                    post.upVote(false);
                userPostVote.setVoteType(VoteTypeEnum.DOWN_VOTED);
                post.downVote(true);
            }
        }
        makeUserContributor(user, post.getGroup());
        postService.save(post);
        userPostVoteRepository.save(userPostVote);
        if (!optionalUserPostVote.isPresent())
            elasticsearchPublisher.publishPostVote(userPostVote, ESType.CREATE);
        else elasticsearchPublisher.publishPostVote(userPostVote, ESType.UPDATE);
        return new HashMap<String, Integer>() {{
            put("up", post.getUpvotes());
            put("down", post.getDownvotes());
        }};
    }

    /**
     * Report a post
     * @param report report
     * @param postId post id
     * @param body request body containing the text
     */
    @RequestMapping(value = "/report/post/{post_id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(value = HttpStatus.OK)
    public void reportPost(@Valid Report report, @PathVariable("post_id") Integer postId, @RequestBody String body) {
        User accuser = Methods.getLoggedInUser(userService);
        // Get report text from body
        for (String e : body.split("&")) {
            if (e.contains("message=")) {
                report.setText(e.split("=")[1]);
                break;
            }
        }
        Post post = postService.findById(postId);
        report.setPost(post);
        report.setAccuser(accuser);
        report.setStatus(ReportStatus.NOT_DECIDED);
        report.setReported(post.getAuthor());
        logger.debug("Reported post " + report.getPost().getId() + " by " + report.getAccuser().getId());
        reportService.createReport(report);
    }
}
