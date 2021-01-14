package com.socatel.services.organisation;

import com.socatel.models.Invitation;
import com.socatel.models.Organisation;
import com.socatel.models.User;
import com.socatel.utils.enums.InvitationStatus;
import com.socatel.utils.enums.OrgRoleEnum;

public interface OrganisationService {
    void createOrganisation(Organisation organisation, User user);
    Organisation findByName(String name);
    void save(Organisation organisation);
    void inviteMemberToOrganisation(String email, User user, OrgRoleEnum role, String appUrl);
    Invitation findInvitationByToken(String token);
    void updateInvitationStatus(Invitation invitation, InvitationStatus status);

    void setTwitterData(Organisation organisation, String screenName, String accountDescription, Long userId, String oauthToken, String oauthSecret);

    void removeTwitterData(Organisation organisation);
}
