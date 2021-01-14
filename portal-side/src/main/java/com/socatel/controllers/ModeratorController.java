package com.socatel.controllers;

import com.socatel.configurations.ThreadPoolTaskSchedulerConfig;
import com.socatel.models.Post;
import com.socatel.models.Report;
import com.socatel.models.Service;
import com.socatel.models.User;
import com.socatel.services.group.GroupService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.post.PostService;
import com.socatel.services.proposition.PropositionService;
import com.socatel.services.report.ReportService;
import com.socatel.services.service.ServiceService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.EnableEnum;
import com.socatel.utils.enums.ReportStatus;
import com.socatel.utils.enums.ServiceStatus;
import com.socatel.utils.enums.VisibleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Calendar;
import java.util.List;

@Controller
@PreAuthorize("hasRole('ROLE_MODERATOR')")
public class ModeratorController {

    @Autowired private ReportService reportService;
    @Autowired private GroupService groupService;
    @Autowired private PostService postService;
    @Autowired private PropositionService propositionService;
    @Autowired private ServiceService serviceService;
    @Autowired private NotificationService notificationService;
    @Autowired private UserService userService;
    @Autowired private ThreadPoolTaskSchedulerConfig.GroupScheduler groupScheduler;
    @Autowired private MessageSource messageSource;

    private Logger logger = LoggerFactory.getLogger(ModeratorController.class);

    /**
     * Mask reported contribution
     * @param reportId report id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/mask/{report_id}")
    public void maskContribution(@PathVariable(value = "report_id") Integer reportId) {
        logger.debug("Mask contribution, reportId " + reportId);
        Report report = reportService.findById(reportId);
        List<Report> reports;
        int id;
        if (report.isPost()) {
            id = report.getPost().getId();
            postService.mask(id);
            reports = reportService.findByPostId(id);
            logger.debug("Masked post " + id);
        }
        else {
            id = report.getProposition().getId();
            propositionService.mask(id);
            reports = reportService.findByPropositionId(id);
            logger.debug("Masked proposition " + id);
        }
        updateReportStatus(reports);
    }

    /**
     * Unmask reported contribution
     * @param id report id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/unmask/{report_id}")
    public void unmaskContribution(@PathVariable(value = "report_id") Integer id) {
        Report report = reportService.findById(id);
        if (report.isPost()) postService.unmask(report.getPost().getId());
        else propositionService.unmask(report.getProposition().getId());
        logger.debug("Unmasked contribution report " + id);
    }

    /**
     * Remove reports
     * @param id report id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/remove-reports/{report_id}")
    public void removeReports(@PathVariable(value = "report_id") Integer id) {
        Report report = reportService.findById(id);
        if (report.isPost()) reportService.deleteAll(reportService.findByPostId(report.getPost().getId()));
        else reportService.deleteAll(reportService.findByPostId(report.getProposition().getId()));
        logger.debug("Removed reports like " + id);
    }

    /**
     * Remove report
     * @param id report id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/remove-report/{report_id}")
    public void removeReport(@PathVariable(value = "report_id") Integer id) {
        Report report = reportService.findById(id);
        report.setStatus(ReportStatus.DECIDED);
        reportService.save(report);
        logger.debug("Removed report " + id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/proposals/{id}/accept")
    public void acceptProposal(@PathVariable(value = "id") Integer id) {
        logger.debug("Accepted proposal, id " + id);
        Post post = postService.findById(id);
        post.setVisible(VisibleEnum.VISIBLE);
        postService.save(post);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/proposals/{id}/reject")
    public void rejectProposal(@PathVariable(value = "id") Integer id) {
        logger.debug("Rejected proposal, id " + id);
        Post post = postService.findById(id);
        post.setVisible(VisibleEnum.HIDDEN);
        postService.save(post);
    }

    /**
     * Accept service
     * @param id service id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/accept-service/{service_id}")
    public void acceptService(@PathVariable(value = "service_id") Integer id) {
        logger.debug("Accepted service, id " + id);
        Service service = serviceService.findById(id);
        service.setStatus(ServiceStatus.APPROVED);
        serviceService.save(service);
    }

    /**
     * Reject service
     * @param id service id
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/moderator/reject-service/{service_id}")
    public void rejectService(@PathVariable(value = "service_id") Integer id) {
        logger.debug("Rejected service, id " + id);
        Service service = serviceService.findById(id);
        // TODO add rejection reason
        service.setStatus(ServiceStatus.REJECTED);
        serviceService.save(service);
    }

    /**
     * Ban user
     * @param username username
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = {"/moderator/ban-user/{username}"})
    public void banUser(@PathVariable(value = "username") String username) {
        User user = userService.findByUsername(username);
        if (user == null)  throw new UsernameNotFoundException(username);
        user.setEnabled(EnableEnum.LOCKED);
        userService.save(user);
        updateReportStatus(reportService.findByReported(user));
        logger.debug("Ban user " + user.getId() + " " + username);
    }

    /**
     * Unban user
     * @param username username
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = {"/moderator/unban-user/{username}"})
    public void unBanUser(@PathVariable(value = "username") String username) {
        User user = userService.findByUsername(username);
        if (user == null)  throw new UsernameNotFoundException(username);
        user.setEnabled(EnableEnum.ENABLED);
        userService.save(user);
        logger.debug("Unban user " + user.getId() + " " + username);
    }

    /**
     * Update report status to Decided
     * @param reports list of reports
     */
    private void updateReportStatus(List<Report> reports) {
        for (Report report : reports)
            report.setStatus(ReportStatus.DECIDED);
        reportService.saveAll(reports);
    }

    /**
     * Schedule an unban
     */
    private void scheduleUnBan() {
        // TODO delete if not temporary ban
        Calendar cal = Calendar.getInstance();
        // Perma ban (200 years)
        cal.add(Calendar.YEAR, 200);
        //ScheduledFuture<?> future = taskScheduler.schedule(new UnBanRunnable(username), cal.getTime());
        //scheduledTasks.add(future);
    }

    /**
     * Unban runnable class
     */
    private class UnBanRunnable implements Runnable {
        private String username;
        UnBanRunnable(String username) {
            this.username = username;
        }

        /**
         * Unban
         */
        public void run() {
            User user = userService.findByUsername(username);
            if (user != null) {
                user.setEnabled(EnableEnum.ENABLED);
                userService.save(user);
            }
        }
    }

}
