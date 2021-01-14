package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.models.*;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.models.relationships.PropositionUserVote;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.PropositionUserVoteRepository;
import com.socatel.services.document.DocumentService;
import com.socatel.services.feedback.FeedbackService;
import com.socatel.services.group.GroupService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.proposition.PropositionService;
import com.socatel.services.report.ReportService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.ESType;
import com.socatel.utils.enums.GroupUserRelationEnum;
import com.socatel.utils.enums.PropositionTypeEnum;
import com.socatel.utils.enums.VoteTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

@Controller
public class PropositionController {

    @Autowired private GroupService groupService;
    @Autowired private PostService postService;
    @Autowired private PropositionService propositionService;
    @Autowired private UserService userService;
    @Autowired private PropositionUserVoteRepository propositionUserVoteRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private ReportService reportService;
    @Autowired private ElasticsearchPublisher elasticsearchPublisher;
    @Autowired private FeedbackService feedbackService;
    @Autowired private DocumentService documentService;

    private Logger logger = LoggerFactory.getLogger(PropositionController.class);

    /**
     * Create a new proposition (step 2)
     * @param proposition proposition
     * @param groupId group id
     * @param postId post id
     * @param typePos position of type in the enum
     * @return redirect to group
     */
    @PreAuthorize(value = "isAuthenticated()")
    @RequestMapping(value = {"/topic/{group_id}/post/{post_id}/propose/{t}", "/topic.html/{group_id}/propose{t}"}, method = RequestMethod.POST)
    public String proposeInStep2(@Valid Proposition proposition, @PathVariable(value = "group_id") Integer groupId, @PathVariable(value = "post_id") Integer postId, @PathVariable("t") Integer typePos) {
        User user = Methods.getLoggedInUser(userService);
        Group group = groupService.findById(groupId);
        // Contributor when contributing
        makeUserContributor(user, group);
        Post post = postService.findById(postId);
        proposition.setType(PropositionTypeEnum.values()[typePos-1]);
        proposition.setUser(user);
        proposition.setPost(post);
        proposition.setTimestamp(new Timestamp(System.currentTimeMillis()));
        logger.debug("Posted new proposition " + proposition.getId() + " in group " + groupId);
        propositionService.save(proposition);
        return "redirect:/topic/" + groupId + "/step2#tabs";
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
     * Up vote a proposition
     * @param propositionId proposition id
     * @return OK status with number of upvotes and downvotes
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/proposition/{proposition_id}/upvote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity upVoteProposition(@PathVariable(value = "proposition_id") Integer propositionId) {
        return ResponseEntity.ok(voteProposition(true, propositionId));
    }

    /**
     * Down vote a proposition
     * @param propositionId proposition id
     * @return OK status with number of upvotes and downvotes
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/proposition/{proposition_id}/downvote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity downVoteProposition(@PathVariable(value = "proposition_id") Integer propositionId) {
        return ResponseEntity.ok(voteProposition(false, propositionId));
    }

    /**
     * Vote up/down a proposition
     * @param isUpVote is upvote (true) or downvote (false)
     * @param propositionId proposition id
     */
    private HashMap<String, Integer> voteProposition(boolean isUpVote, Integer propositionId) {
        User user = Methods.getLoggedInUser(userService);
        Proposition proposition = propositionService.findById(propositionId);
        Optional<PropositionUserVote> optionalPropositionUserVote = propositionUserVoteRepository.findByUserAndProposition(user, proposition);
        PropositionUserVote propositionUserVote = optionalPropositionUserVote.orElse(new PropositionUserVote(user, proposition));
        if (isUpVote) {
            logger.debug("Upvoted proposition " + propositionId + " by " + user.getUsername());
            if (propositionUserVote.getVoteType().equals(VoteTypeEnum.UP_VOTED)) {
                propositionUserVote.setVoteType(VoteTypeEnum.NO_VOTED);
                proposition.upVote(false);
            } else {
                if (propositionUserVote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED))
                    proposition.downVote(false);
                propositionUserVote.setVoteType(VoteTypeEnum.UP_VOTED);
                proposition.upVote(true);
            }
        } else {
            logger.debug("Downvoted proposition " + propositionId + " by " + user.getUsername());
            if (propositionUserVote.getVoteType().equals(VoteTypeEnum.DOWN_VOTED)) {
                propositionUserVote.setVoteType(VoteTypeEnum.NO_VOTED);
                proposition.downVote(false);
            } else {
                if (propositionUserVote.getVoteType().equals(VoteTypeEnum.UP_VOTED))
                    proposition.upVote(false);
                propositionUserVote.setVoteType(VoteTypeEnum.DOWN_VOTED);
                proposition.downVote(true);
            }
        }
        makeUserContributor(user, postService.findById(proposition.getPost().getId()).getGroup());
        propositionService.save(proposition);
        propositionUserVoteRepository.save(propositionUserVote);
        if (!optionalPropositionUserVote.isPresent())
            elasticsearchPublisher.publishPropositionVote(propositionUserVote, ESType.CREATE);
        else elasticsearchPublisher.publishPropositionVote(propositionUserVote, ESType.UPDATE);
        return new HashMap<String, Integer>() {{
            put("up", proposition.getUpvotes());
            put("down", proposition.getDownvotes());
        }};
    }

    /**
     * Report a proposition
     * @param report report
     */
    @RequestMapping(value = "/report-proposition")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(value = HttpStatus.OK)
    public void reportProposition(@Valid Report report) {
        logger.debug("Reported proposition " + report.getProposition().getId() + " by " + report.getAccuser().getId());
        reportService.createReport(report);
    }

    /**
     * Delete a proposition
     * @param propositionId proposition id
     */
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"/proposition/{proposition_id}/delete"})
    @PreAuthorize("isAuthenticated()")
    public void deleteProposition(@PathVariable(value = "proposition_id") Integer propositionId) {
        Proposition proposition = propositionService.findById(propositionId);
        User user = Methods.getLoggedInUser(userService);
        if (user.isModerator()) {
            propositionService.mask(proposition.getId());
            logger.debug("Deleted proposition " + proposition.getId());
        }
    }
}
