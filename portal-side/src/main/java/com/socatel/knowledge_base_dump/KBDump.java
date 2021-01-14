package com.socatel.knowledge_base_dump;

import com.socatel.components.Methods;
import com.socatel.dtos.knowledge_base.GroupIdDTO;
import com.socatel.dtos.knowledge_base.SkillIdDTO;
import com.socatel.dtos.knowledge_base.ThemeIdDTO;
import com.socatel.models.*;
import com.socatel.models.relationships.PropositionUserVote;
import com.socatel.models.relationships.UserPostVote;
import com.socatel.repositories.GroupRepository;
import com.socatel.repositories.GroupUserRelationRepository;
import com.socatel.repositories.SkillRepository;
import com.socatel.repositories.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.LinkedList;
import java.util.List;

@Component
public class KBDump {

    @Autowired private ThemeRepository themeRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private GroupUserRelationRepository groupUserRelationRepository;
    @PersistenceContext private EntityManager entityManager;

    public DbUserRowDump dumpSingleUserRow(User user) {
        int userId = user.getId();
        List<ThemeIdDTO> themes = themeRepository.findThemesByUserId(userId);
        List<Group> groups = groupRepository.findAllByUserId(userId);
        List<SkillIdDTO> skills = skillRepository.findSkillsByUserId(userId);
        List<GroupIdDTO> groupsDTO = new LinkedList<>();

        for (Group g : groups) {
            groupsDTO.add(new GroupIdDTO(g.getId()));
        }
        return new DbUserRowDump(
                Methods.encrypt(userId),
                user.getLocality(),
                user.getFirstLang(),
                user.getSecondLang(),
                user.getOrganisation() != null ? user.getOrganisation().getId() : null,
                themes,
                groupsDTO,
                skills
        );
    }

    public DbGroupRowDump dumpSingleGroupRow(Group group) {
        List<ThemeIdDTO> themes = themeRepository.findThemesByGroupId(group.getId());
        List<User> usersList = (List<User>)groupUserRelationRepository.findByGroupId(group.getId());
        List<String> users = new LinkedList<>();
        for (User u : usersList) {
            users.add(Methods.encrypt(u.getId()));
        }
        return new DbGroupRowDump(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getStatus(),
                group.getTimestamp(),
                group.getNextStepTimestamp(),
                group.getLocality(),
                group.getLanguage(),
                Methods.encrypt(group.getInitiator().getId()),
                themes,
                users
        );
    }

    public DbPostRowDump dumpSinglePostRow(Post post) {
        return new DbPostRowDump(
                post.getId(),
                post.getText(),
                post.getTimestamp(),
                post.getUpvotes(),
                post.getDownvotes(),
                post.getPostType(),
                post.getPostPhase(),
                post.getVisible(),
                post.getPin(),
                post.getPostParent() != null ? post.getPostParent().getId() : null,
                Methods.encrypt(post.getAuthor().getId()),
                post.getGroup().getId(),
                post.getOrganisation() != null ? post.getOrganisation().getId() : null
        );
    }

    public DbPropositionRowDump dumpSinglePropositionRow(Proposition proposition) {
        return new DbPropositionRowDump(
                proposition.getId(),
                proposition.getText(),
                proposition.getType(),
                proposition.getUpvotes(),
                proposition.getDownvotes(),
                proposition.getVisible(),
                proposition.getPin(),
                proposition.getTimestamp(),
                proposition.getPost() == null ? null : proposition.getPost().getId(),
                Methods.encrypt(proposition.getUser().getId())
        );
    }

    public DbOrganisationRowDump dumpSingleOrganisationRow(Organisation organisation) {
        return new DbOrganisationRowDump(
                organisation.getId(),
                organisation.getName(),
                organisation.getStructure(),
                organisation.getWebsite(),
                organisation.getTwitterScreenName(),
                organisation.getTwitterAccountDescription(),
                organisation.getTwitterUserId(),
                organisation.getTwitterOauthToken(),
                organisation.getTwitterOauthSecret(),
                organisation.getFacebookPageId(),
                organisation.getFacebookOauthToken()
        );
    }

    public DbUserPostVoteRowDump dumpSingleUserPostVoteRow(UserPostVote userPostVote) {
        return new DbUserPostVoteRowDump(
                Methods.encrypt(userPostVote.getUserId()),
                userPostVote.getPostId(),
                userPostVote.getVoteType()
        );
    }

    public DbUserPropositionVoteRowDump dumpSingleUserPropositionVoteRow(PropositionUserVote propositionUserVote) {
        return new DbUserPropositionVoteRowDump(
                Methods.encrypt(propositionUserVote.getUserId()),
                propositionUserVote.getPropositionId(),
                propositionUserVote.getVoteType()
        );
    }

    public DbHistoryRowDump dumpSingleHistoryRow(History history) {
        return new DbHistoryRowDump(
                history.getId(),
                history.getText(),
                history.getTimestamp(),
                history.getType(),
                history.getLevel(),
                history.getUser() != null ? Methods.encrypt(history.getUser().getId()) : null,
                history.getOrganisation() != null ? history.getOrganisation().getId() : null,
                history.getGroup() != null ? history.getGroup().getId() : null
        );
    }

    public DbServiceRowDump dumpSingleServiceRow(Service service) {
        List<ThemeIdDTO> themes = themeRepository.findThemesByServiceId(service.getId());
        return new DbServiceRowDump(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getNativeDescription(),
                service.getWebsite(),
                service.getLocality(),
                service.getLanguage(),
                service.getOrganisation() != null ? service.getOrganisation().getId() : null,
                service.getGroup() != null ? service.getGroup().getId() : null,
                service.getStatus(),
                service.getHashtag(),
                themes,
                service.getTwitterScreenName(),
                service.getTwitterAccountDescription(),
                service.getTwitterUserId(),
                service.getTwitterOauthToken(),
                service.getTwitterOauthSecret(),
                service.getFacebookPageId(),
                service.getFacebookOauthToken()
        );
    }
}
