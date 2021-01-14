package com.socatel.services.organisation;

import com.socatel.models.Invitation;
import com.socatel.models.Organisation;
import com.socatel.models.User;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.InvitationRepository;
import com.socatel.repositories.OrganisationRepository;
import com.socatel.services.email.EmailService;
import com.socatel.services.history.HistoryService;
import com.socatel.utils.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class OrganisationServiceImpl implements OrganisationService {

    private OrganisationRepository organisationRepository;
    private InvitationRepository invitationRepository;
    private ElasticsearchPublisher elasticsearchPublisher;
    private EmailService emailService;
    private MessageSource messageSource;
    private HistoryService historyService;

    @Autowired
    public OrganisationServiceImpl(OrganisationRepository organisationRepository,
                                   InvitationRepository invitationRepository,
                                   ElasticsearchPublisher elasticsearchPublisher,
                                   EmailService emailService,
                                   MessageSource messageSource,
                                   HistoryService historyService) {
        this.organisationRepository = organisationRepository;
        this.invitationRepository = invitationRepository;
        this.elasticsearchPublisher = elasticsearchPublisher;
        this.emailService = emailService;
        this.messageSource = messageSource;
        this.historyService = historyService;
    }

    @Override
    public void createOrganisation(Organisation organisation, User user) {
        user.setOrgRole(OrgRoleEnum.ADMIN);
        user.setOrganisation(organisation);
        Organisation newOrg = organisationRepository.save(organisation);
        historyService.createHistory(user, newOrg, null, "history.created_organisation", HistoryTypeEnum.CREATED_ORGANISATION, VisibleEnum.VISIBLE);
        elasticsearchPublisher.publishOrganisation(newOrg, ESType.CREATE);
        elasticsearchPublisher.publishUser(user, ESType.UPDATE);
    }

    @Override
    public Organisation findByName(String name) {
        return organisationRepository.findByName(name).orElse(null);
    }

    @Override
    public void save(Organisation organisation) {
        boolean newPost = organisation.getId() == 0;
        Organisation saved = organisationRepository.save(organisation);
        if (newPost) elasticsearchPublisher.publishOrganisation(saved, ESType.CREATE);
        else elasticsearchPublisher.publishOrganisation(saved, ESType.UPDATE);
    }

    @Override
    public void inviteMemberToOrganisation(String email, User user, OrgRoleEnum role, String appUrl) {
        String from = user.getUsername();
        Locale locale = new Locale(user.getFirstLang().getCode());
        String subject = messageSource.getMessage("email.invitation.subject", null, locale);
        String token = UUID.randomUUID().toString();
        String message =
                messageSource.getMessage("email.invitation.body", new Object[] {user.getOrganisation().getName(), from}, locale) + "\n" +
                messageSource.getMessage("email.invitation.body1", new Object[] {appUrl + "/registration?token=" + token}, locale) + "\n" +
                messageSource.getMessage("email.invitation.body2", new Object[] {appUrl + "/accept-invitation?token=" + token}, locale) + "\n" +
                messageSource.getMessage("email.invitation.body3", new Object[] {appUrl + "/reject-invitation?token=" + token}, locale);
        emailService.sendEmail(email, subject, message, locale);
        Invitation invitation = new Invitation(token, email, user, user.getOrganisation(), role);
        invitationRepository.save(invitation);
    }

    @Override
    public Invitation findInvitationByToken(String token) {
        return invitationRepository.findByToken(token).orElse(null);
    }

    @Override
    public void updateInvitationStatus(Invitation invitation, InvitationStatus status) {
        invitation.setStatus(status);
        invitationRepository.save(invitation);
    }

    @Override
    public void setTwitterData(Organisation organisation, String screenName, String accountDescription, Long userId, String oauthToken, String oauthSecret) {
        organisation.setTwitterScreenName(screenName);
        organisation.setTwitterAccountDescription(accountDescription);
        organisation.setTwitterUserId(userId);
        organisation.setTwitterOauthToken(oauthToken);
        organisation.setTwitterOauthSecret(oauthSecret);
        organisationRepository.save(organisation);
    }

    @Override
    public void removeTwitterData(Organisation organisation) {
        organisation.setTwitterScreenName(null);
        organisation.setTwitterAccountDescription(null);
        organisation.setTwitterUserId(null);
        organisation.setTwitterOauthToken(null);
        organisation.setTwitterOauthSecret(null);
        organisationRepository.save(organisation);
    }

}
