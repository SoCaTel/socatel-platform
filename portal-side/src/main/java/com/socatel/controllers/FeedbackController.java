package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.models.*;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.models.relationships.UserFeedbackVote;
import com.socatel.repositories.UserFeedbackVoteRepository;
import com.socatel.services.answer.AnswerService;
import com.socatel.services.document.DocumentService;
import com.socatel.services.feedback.FeedbackService;
import com.socatel.services.group.GroupService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.GroupUserRelationEnum;
import com.socatel.utils.enums.VisibleEnum;
import com.socatel.utils.enums.VoteTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

@Controller
public class FeedbackController {

    @Autowired private DocumentService documentService;
    @Autowired private GroupService groupService;
    @Autowired private UserService userService;
    @Autowired private PostService postService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private UserFeedbackVoteRepository userFeedbackVoteRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private AnswerService answerService;

    private Logger logger = LoggerFactory.getLogger(PostController.class);

    /**
     * Request feedback in group STEP4
     * @param feedback new feedback
     * @param groupId group id
     * @param postId post id
     * @param request request
     * @param files files attached
     * @return refresh page
     */
    @RequestMapping(value = {"/topic/{group_id}/post/{post_id}/feedback", "/topic.html/{group_id}/post/{post_id}/feedback"}, method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public String requestFeedback(@Valid Feedback feedback,
                                  @RequestParam("answers[]") Optional<String[]> answers,
                                  @PathVariable(value = "group_id") Integer groupId,
                                  @PathVariable(value = "post_id") Integer postId,
                                  HttpServletRequest request,
                                  @RequestParam("file") Optional<MultipartFile[]> files) {
        User user = Methods.getLoggedInUser(userService);
        Group group = groupService.findById(groupId);
        Answer answer;

        // Contributor when contributing
        GroupUserRelation relation = groupService.findGroupRelation(group, user);
        if (relation == null)
            groupService.createGroupUserRelation(group, user, GroupUserRelationEnum.CONTRIBUTOR);
        else if (relation.getRelation().equals(GroupUserRelationEnum.SUBSCRIBED))
            groupService.changeRelationWithUser(group, user, GroupUserRelationEnum.CONTRIBUTOR);

        // Set Feedback
        Post post = postService.findById(postId);
        feedback.setPost(post);
        feedback.setGroup(group);
        feedback.setUser(user);
        feedback.setVisible(VisibleEnum.VISIBLE);
        feedback.setTimestamp(new Timestamp(System.currentTimeMillis()));
        feedback = feedbackService.save(feedback);

        if (answers.isPresent()) {
            for (String a: answers.get()) {
                answer = new Answer(a, feedback);
                answerService.save(answer);
            }
        }

        // Attach files to feedback
        if (files.isPresent()) {
            for (MultipartFile file: files.get())
                if (!file.isEmpty())
                    documentService.storeFile(file, null, null, null, feedback.getId(), null, null, new Locale(user.getFirstLang().getCode()));
        }

        // Notify users
        notificationService.notifyAllParticipantsInGroup(groupId, "notification.participant.new_feedback", "/topic/" + groupId + "/step4", group.getName());

        logger.debug("Requested new feedback " + feedback.getId() + " in group " + groupId);

        return "redirect:/topic/" + groupId + "/step4#tabs";
    }

    /**
     * Mask feedback
     * @param feedbackId feedback id
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"/feedback/{feedback_id}/archive"})
    @PreAuthorize("isAuthenticated()")
    public void maskFeedback(@PathVariable(value = "feedback_id") Integer feedbackId) {
        Feedback feedback = feedbackService.findFeedbackById(feedbackId);
        if(feedback.getVisible().equals(VisibleEnum.VISIBLE))
            feedback.setVisible(VisibleEnum.HIDDEN);
        else feedback.setVisible(VisibleEnum.VISIBLE);
        feedbackService.save(feedback);
    }

    /**
     * Up vote a feedback
     * @param feedbackId feedback id
     * @return OK status
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/feedback/{feedback_id}/upvote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity upVoteFeedback(@PathVariable(value = "feedback_id") Integer feedbackId) {
        return ResponseEntity.ok(voteFeedback(true, feedbackId));
    }
    /**
     * Down vote a feedback
     * @param feedbackId feedback id
     * @return OK status
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/feedback/{feedback_id}/downvote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity downVoteFeedback(@PathVariable(value = "feedback_id") Integer feedbackId) {
        return ResponseEntity.ok(voteFeedback(false, feedbackId));
    }

    /**
     * Vote a feedback
     * @param isUpVote is upvote or downvote
     * @param feedbackId feedback id
     * @return number of upvotes and downvotes
     */
    private HashMap<String, Integer> voteFeedback(boolean isUpVote, Integer feedbackId) {
        User user = Methods.getLoggedInUser(userService);
        Feedback feedback = feedbackService.findFeedbackById(feedbackId);
        Optional<UserFeedbackVote> optionalUserFeedbackVote = userFeedbackVoteRepository.findByUserAndFeedback(user, feedback);
        UserFeedbackVote userFeedbackVote = optionalUserFeedbackVote.orElse(new UserFeedbackVote(user, feedback));
        if (isUpVote) {
            logger.debug("Upvoted feedback " + feedbackId + " by " + user.getUsername());
            if (userFeedbackVote.getVoteType().equals(VoteTypeEnum.UP_VOTED)) {
                userFeedbackVote.setVoteType(VoteTypeEnum.NO_VOTED);
            } else {
                userFeedbackVote.setVoteType(VoteTypeEnum.UP_VOTED);
            }
        } else {
            logger.debug("Downvoted post " + feedbackId + " by " + user.getUsername());
            if (userFeedbackVote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED)) {
                userFeedbackVote.setVoteType(VoteTypeEnum.NO_VOTED);
            } else {
                userFeedbackVote.setVoteType(VoteTypeEnum.DOWN_VOTED);
            }
        }
        feedbackService.save(feedback);
        userFeedbackVoteRepository.save(userFeedbackVote);

        return new HashMap<String, Integer>() {{
            put("up", userFeedbackVoteRepository.countByFeedbackAndVoteType(feedback, VoteTypeEnum.UP_VOTED));
            put("down", userFeedbackVoteRepository.countByFeedbackAndVoteType(feedback, VoteTypeEnum.DOWN_VOTED));
        }};
    }
}
