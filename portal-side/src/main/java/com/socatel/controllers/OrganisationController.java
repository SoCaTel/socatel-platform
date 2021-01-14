package com.socatel.controllers;

import com.socatel.components.Methods;
import com.socatel.models.Invitation;
import com.socatel.models.Service;
import com.socatel.models.User;
import com.socatel.services.history.HistoryService;
import com.socatel.services.notification.NotificationService;
import com.socatel.services.organisation.OrganisationService;
import com.socatel.services.service.ServiceService;
import com.socatel.services.user.UserService;
import com.socatel.utils.enums.HistoryTypeEnum;
import com.socatel.utils.enums.InvitationStatus;
import com.socatel.utils.enums.OrgRoleEnum;
import com.socatel.utils.enums.VisibleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class OrganisationController {

    @Autowired private UserService userService;
    @Autowired private OrganisationService organisationService;
    @Autowired private ServiceService serviceService;
    @Autowired private HistoryService historyService;
    @Autowired private NotificationService notificationService;

    private Logger logger = LoggerFactory.getLogger(OrganisationController.class);

    /**
     * Invite member to organisation
     * @param invitation invitation
     * @param role role in the organisation
     * @param request servlet request
     * @return redirect to organisation
     */
    @RequestMapping(value = "/organisation-invite", method = RequestMethod.POST) // TODO Check user already belongs to it
    public ModelAndView inviteMember(@RequestParam("invitation") Optional<String> invitation, @RequestParam("role") Optional<Boolean> role, HttpServletRequest request) {
        String to = invitation.orElse(null);
        // Check if empty or null -> Not invited
        if (to == null || to.isEmpty())
            return new ModelAndView("redirect:/organisation", "invited", false);
        OrgRoleEnum orgRole = role.isPresent() && role.get() ? OrgRoleEnum.ADMIN : OrgRoleEnum.MEMBER;
        User user = Methods.getLoggedInUser(userService);
        String appUrl = request.getRequestURL().toString().replace("/organisation-invite", "");
        organisationService.inviteMemberToOrganisation(to, user, orgRole, appUrl);
        logger.debug("Sent invitation to join organisation " + user.getOrganisation().getName() + " by user " + user.getUsername() + " to " + to);
        return new ModelAndView("redirect:/organisation", "invited", true);
    }

    /**
     * Accept invitation
     * @param token token
     * @return redirect to organisation
     */
    @RequestMapping(value = "/accept-invitation", method = RequestMethod.GET)
    public String acceptInvitation(@RequestParam("token") String token) {
        Invitation invitation = organisationService.findInvitationByToken(token);
        if (invitation != null && invitation.getStatus().compareTo(InvitationStatus.SENT) == 0) {
            User user = userService.findByEmail(invitation.getEmail());
            if (user == null) throw new UsernameNotFoundException(invitation.getEmail());
            user.setOrganisation(invitation.getOrganisation());
            user.setOrgRole(invitation.getRole());
            organisationService.updateInvitationStatus(invitation, InvitationStatus.ACCEPTED);
            userService.save(user);
            historyService.createHistory(user, invitation.getOrganisation(), null, "history.joined_organisation", HistoryTypeEnum.JOINED_ORGANISATION, VisibleEnum.VISIBLE);
            logger.debug("Accepted invitation " + user.getUsername());
        }
        return "redirect:/organisation";
    }

    /**
     * Reject invitation
     * @param token token
     */
    @RequestMapping(value = "/reject-invitation", method = RequestMethod.GET)
    public void rejectInvitation(@RequestParam("token") String token) {
        Invitation invitation = organisationService.findInvitationByToken(token);
        if (invitation != null && invitation.getStatus().compareTo(InvitationStatus.SENT) == 0) {
            organisationService.updateInvitationStatus(invitation, InvitationStatus.REJECTED);
            logger.debug("Rejected invitation " + invitation.getEmail());
        }
        // TODO add page to say group rejected?
    }

    /**
     * Regiser service
     * @param service service
     * @return redirect to organisation
     */
    @RequestMapping(value = "/register-service", method = RequestMethod.POST)
    public String registerService(@Valid Service service) {
        User user = Methods.getLoggedInUser(userService);
        serviceService.registerService(service, user.getOrganisation());
        logger.debug("Register service " + service.getId() + " by " + user.getUsername());
        notificationService.notifyModerators("notification.moderator.suggested_service", "/moderator", service.getName());
        return "redirect:/organisation";
    }

}
