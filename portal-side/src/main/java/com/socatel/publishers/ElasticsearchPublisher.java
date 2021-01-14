package com.socatel.publishers;

import com.socatel.events.ElasticsearchEvent;
import com.socatel.knowledge_base_dump.*;
import com.socatel.models.*;
import com.socatel.models.relationships.PropositionUserVote;
import com.socatel.models.relationships.UserPostVote;
import com.socatel.rest_api.RestAPICaller;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.ESType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchPublisher {
    @Autowired private KBDump kbDump;
    @Autowired private ApplicationEventMulticaster applicationEventMulticaster;
    @Autowired private RestAPICaller restAPICaller;

    public void publishUser(User user, ESType type) {
        restAPICaller.insertUser(user);
        /*DbUserRowDump kbUser = kbDump.dumpSingleUserRow(user);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbUser,
                type,
                Constants.USER_TABLE,
                kbUser.getUser_id()));*/
    }

    public void publishGroup(Group group, ESType type) {
        restAPICaller.insertGroup(group);
        /*DbGroupRowDump kbGroup = kbDump.dumpSingleGroupRow(group);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbGroup,
                type,
                Constants.GROUP_TABLE,
                kbGroup.getGroup_id()));*/
    }

    public void publishOrganisation(Organisation organisation, ESType type) {
        restAPICaller.insertOrganisation(organisation);
        /*DbOrganisationRowDump kbOrganisation = kbDump.dumpSingleOrganisationRow(organisation);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbOrganisation,
                type,
                Constants.ORGANISATION_TABLE,
                kbOrganisation.getOrganisation_id()));*/
    }

    public void publishHistory(History history, ESType type) {
        DbHistoryRowDump kbHistory = kbDump.dumpSingleHistoryRow(history);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbHistory,
                type,
                Constants.HISTORY_TABLE,
                kbHistory.getHistory_id()));
    }

    public void publishPost(Post post, ESType type) {
        DbPostRowDump kbPost = kbDump.dumpSinglePostRow(post);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbPost,
                type,
                Constants.POST_TABLE,
                kbPost.getPost_id()));
    }

    public void publishProposition(Proposition proposition, ESType type) {
        DbPropositionRowDump kbProposition = kbDump.dumpSinglePropositionRow(proposition);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbProposition,
                type,
                Constants.PROPOSITION_TABLE,
                kbProposition.getProposition_id()
        ));
    }

    public void publishService(Service service, ESType type) {
        restAPICaller.insertService(service);
        /*DbServiceRowDump kbService = kbDump.dumpSingleServiceRow(service);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbService,
                type,
                Constants.SERVICE_TABLE,
                kbService.getService_id()
        ));*/
    }

    public void publishPostVote(UserPostVote userPostVote, ESType type) {
        DbUserPostVoteRowDump kbPostVote = kbDump.dumpSingleUserPostVoteRow(userPostVote);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbPostVote,
                type,
                Constants.USER_POST_VOTE_TABLE,
                kbPostVote.getId()
        ));
    }

    public void publishPropositionVote(PropositionUserVote userPropositionVote, ESType type) {
        DbUserPropositionVoteRowDump kbPropositionVote = kbDump.dumpSingleUserPropositionVoteRow(userPropositionVote);
        applicationEventMulticaster.multicastEvent(new ElasticsearchEvent<>(
                kbPropositionVote,
                type,
                Constants.USER_PROPOSITION_VOTE_TABLE,
                kbPropositionVote.getId()
        ));
    }

}
