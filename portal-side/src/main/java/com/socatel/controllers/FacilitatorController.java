package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.configurations.ThreadPoolTaskSchedulerConfig;
import com.socatel.models.Group;
import com.socatel.models.Organisation;
import com.socatel.models.Post;
import com.socatel.services.group.GroupService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.GroupStatusEnum;
import com.socatel.utils.enums.OnOffEnum;
import com.socatel.utils.enums.PostPhaseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ROLE_FACILITATOR')")
public class FacilitatorController {

    @Autowired private UserService userService;
    @Autowired private GroupService groupService;
    @Autowired private PostService postService;
    @Autowired private NotificationService notificationService;
    @Autowired private ThreadPoolTaskSchedulerConfig.GroupScheduler groupScheduler;

    private Logger logger = LoggerFactory.getLogger(FacilitatorController.class);

    /**
     * Extend group step duration
     * @param id group id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/extend-group-step/{group_id}")
    public void extendGroupStep(@PathVariable(value = "group_id") Integer id) {
        logger.debug("Extend group step duration, group_id " + id);
        Group group = groupService.findById(id);
        Timestamp timestamp = group.getNextStepTimestamp();
        if (timestamp == null) // if group hasn't started is extended
            timestamp = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        cal.add(Calendar.MONTH, 1);
        timestamp = new Timestamp(cal.getTime().getTime());
        group.setNextStepTimestamp(timestamp);
        groupService.save(group);
        notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_time_extended", "/topic/" + id, group.getName());
        groupScheduler.createNewTask(id, timestamp);
    }

    /**
     * Move group to next step
     * @param id group id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/next-group-step/{group_id}")
    public void nextGroupStep(@PathVariable(value = "group_id") Integer id) {
        logger.debug("Next group step, group_id " + id);
        Group group = groupService.findById(id);
        Timestamp timestamp;
        switch (group.getStatus()) {
            case SUGGESTED: // to Ideation (accept group)
                group.setStatus(GroupStatusEnum.IDEATION);
                group.setFacilitator(Methods.getLoggedInUser(userService));
                groupService.save(group);
                notificationService.notifyUser(group.getInitiator(), "notification.initiator.group_accepted", "/topic/" + group.getId(), group.getName());
                break;
            case IDEATION: // to Validation
                // If no ideas, don't forward
                if(postService.findGroupBestIdea(group.getId()).isEmpty())
                    break;
                timestamp = Methods.oneMonthFromNow(); // 1 month for step 1 (Validation)
                group.setStatus(GroupStatusEnum.VALIDATION);
                group.setNextStepTimestamp(timestamp);
                groupService.save(group);
                groupScheduler.createNewTask(id, timestamp);
                notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_advanced_manually", "/topic/" + id, group.getName());
                break;
            case VALIDATION: // to Codesign
                group.setStatus(GroupStatusEnum.CODESIGN);
                group.setNextStepTimestamp(new Timestamp(System.currentTimeMillis())); // Started Co-design on this date
                groupService.save(group);
                groupScheduler.cancelTask(id);
                notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_advanced_manually", "/topic/" + id, group.getName());
                break;
            case CODESIGN: // to Test
                group.setStatus(GroupStatusEnum.TEST);
                group.setNextStepTimestamp(new Timestamp(System.currentTimeMillis())); // Started Test on this date
                groupService.save(group);
                notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_advanced_manually", "/topic/" + id, group.getName());
                break;
            case TEST: // to Complete
                // If service provider has to be selected
                Post idea = postService.findGroupBestIdea(group.getId()).get(0);
                if (idea.getOrganisation() == null) {
                    // If there is any organisation
                    List<Post> posts = postService.findPostsByGroupIdAndPostPhase(group.getId(), PostPhaseEnum.ORG_APPLY);
                    if (!posts.isEmpty()) {
                        // Select 1st (most voted)
                        Organisation organisation = userService.findById(posts.get(0).getAuthor().getId()).getOrganisation();
                        idea.setOrganisation(organisation);
                        postService.save(idea);
                        notificationService.notifyAllParticipantsInGroup(id, "notification.participant.organisation_selected", "/topic/" + id, group.getName());
                    }
                } else {
                    // If service provider was already selected
                    group.setStatus(GroupStatusEnum.COMPLETED);
                    group.setNextStepTimestamp(new Timestamp(System.currentTimeMillis())); // Completed on this date
                    groupService.save(group);
                    notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_advanced_manually", "/topic/" + id, group.getName());
                }
                break;
        }
    }

    /**
     * Move group to previous step
     * @param id group id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/previous-group-step/{group_id}")
    public void previousGroupStep(@PathVariable(value = "group_id") Integer id) {
        logger.debug("Previous group step, group_id " + id);
        Group group = groupService.findById(id);
        Timestamp timestamp;
        switch (group.getStatus()) {
            case VALIDATION:
                timestamp = Methods.oneMonthFromNow(); // 1 month for step 1 (Ideation)
                group.setStatus(GroupStatusEnum.IDEATION);
                group.setNextStepTimestamp(timestamp);
                groupService.save(group);
                groupScheduler.createNewTask(id, timestamp);
                break;
            case CODESIGN:
                timestamp = Methods.oneMonthFromNow(); // 1 month for step 2 (Validation)
                group.setStatus(GroupStatusEnum.VALIDATION);
                group.setNextStepTimestamp(timestamp);
                groupService.save(group);
                groupScheduler.createNewTask(id, timestamp);
                break;
            case TEST:
                Post idea = postService.findGroupBestIdea(group.getId()).get(0);
                if (idea.getOrganisation() == null) {
                    // If there is not organisation, go to previous step
                    group.setStatus(GroupStatusEnum.CODESIGN);
                    group.setNextStepTimestamp(new Timestamp(System.currentTimeMillis())); // Started again Co-design on this date
                    groupService.save(group);
                } else {
                    // If there is organisation, remove to select organisation
                    idea.setOrganisation(null);
                    postService.save(idea);
                }
                break;
            case COMPLETED:
                group.setStatus(GroupStatusEnum.TEST);
                group.setNextStepTimestamp(new Timestamp(System.currentTimeMillis())); // Started again test on this date
                groupService.save(group);
                break;
        }
        notificationService.notifyAllParticipantsInGroup(id, "notification.participant.group_backwarded_manually", "/topic/" + id, group.getName());
    }

    /**
     * Reject group
     * @param id group id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/reject-group/{group_id}")
    public void rejectGroup(@PathVariable(value = "group_id") Integer id) {
        logger.debug("Rejected group, group_id " + id);
        Group group = groupService.findById(id);
        // TODO add rejection reason
        // Variable in group: group_rejection_reason. Display it in the group page, in the main content page.
        group.setStatus(GroupStatusEnum.REJECTED);
        groupService.save(group);
        notificationService.notifyUser(group.getInitiator(), "notification.initiator.group_rejected", "/topic/" + id, group.getName());
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/facilitator/group-meeting/{group_id}/show")
    public void displayGroupVideoMeeting(@PathVariable(value = "group_id") Integer id) {
        logger.debug("Display group video meeting, group_id " + id);
        Group group = groupService.findById(id);
        group.setMeetingDisplay(OnOffEnum.ON);
        group.setMeetingCode(group.getMeetingCode());
        groupService.save(group);
        notificationService.notifyAllParticipantsInGroup(id, "notification.participant.video_meeting", "/topic/" + id, group.getName());
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/facilitator/group-meeting/{group_id}/hide")
    public void hideGroupVideoMeeting(@PathVariable(value = "group_id") Integer id) {
        logger.debug("Hide group video meeting, group_id " + id);
        Group group = groupService.findById(id);
        group.setMeetingDisplay(OnOffEnum.OFF);
        groupService.save(group);
    }

}
