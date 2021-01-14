package com.socatel.services.group;

import com.socatel.models.Group;
import com.socatel.models.Post;
import com.socatel.models.User;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.publishers.ElasticsearchPublisher;
import com.socatel.repositories.GroupRepository;
import com.socatel.repositories.GroupUserRelationRepository;
import com.socatel.repositories.UserRepository;
import com.socatel.services.history.HistoryService;
import com.socatel.services.post.PostService;
import com.socatel.utils.Constants;
import com.socatel.utils.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService{

    private GroupRepository groupRepository;
    private GroupUserRelationRepository groupUserRelationRepository;
    private ElasticsearchPublisher elasticsearchPublisher;
    private HistoryService historyService;
    private final UserRepository userRepository;
    private final PostService postService;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository,
                            GroupUserRelationRepository groupUserRelationRepository,
                            ElasticsearchPublisher elasticsearchPublisher,
                            HistoryService historyService, UserRepository userRepository, PostService postService) {
        this.groupRepository = groupRepository;
        this.groupUserRelationRepository = groupUserRelationRepository;
        this.elasticsearchPublisher = elasticsearchPublisher;
        this.historyService = historyService;
        this.userRepository = userRepository;
        this.postService = postService;
    }

    @Override
    public Long countAll() {
        return groupRepository.count();
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Override
    public Group findByName(String groupName) {
        return groupRepository.findByName(groupName).orElse(null);
    }

    @Override
    public Group findById(Integer id) { return groupRepository.findById(id).orElse(null); }

    @Override
    public void save(Group group) {
        boolean newGroup = group.getId() == 0;
        Group saved = groupRepository.save(group);
        if (newGroup) elasticsearchPublisher.publishGroup(saved, ESType.CREATE);
        else elasticsearchPublisher.publishGroup(saved, ESType.UPDATE);
    }

    @Override
    public Long countSubscribers(Integer groupId) {
        return groupUserRelationRepository.countByGroupId(groupId);
    }

    @Override
    public Long countContributors(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndRelation(groupId, GroupUserRelationEnum.CONTRIBUTOR) + 1;
    }

    @Override
    public List<User> getContributors(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findContributorsInGroup(groupId);
    }

    @Override
    public Long countOlderPeople(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndUserProfile(groupId, ProfileEnum.OLDER_PERSON);
    }

    @Override
    public List<User> getOlderPeople(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findUsersByGroupIdAndProfile(groupId, ProfileEnum.OLDER_PERSON);
    }

    @Override
    public Long countFamilyFriends(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndUserProfile(groupId, ProfileEnum.FAMILY_FRIEND);
    }

    @Override
    public List<User> getFamilyFriends(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findUsersByGroupIdAndProfile(groupId, ProfileEnum.FAMILY_FRIEND);
    }

    @Override
    public Long countCareAssistants(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndUserProfile(groupId, ProfileEnum.CARE_ASSISTANT);
    }

    @Override
    public List<User> getCareAssistants(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findUsersByGroupIdAndProfile(groupId, ProfileEnum.CARE_ASSISTANT);
    }

    @Override
    public Long countHealthcareSpecialists(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndUserProfile(groupId, ProfileEnum.HEALTHCARE_SPECIALIST);
    }

    @Override
    public List<User> getHealthcareSpecialists(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findUsersByGroupIdAndProfile(groupId, ProfileEnum.HEALTHCARE_SPECIALIST);
    }

    @Override
    public Long countServiceProviders(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndUserProfile(groupId, ProfileEnum.SERVICE_PROVIDER);
    }

    @Override
    public List<User> getServiceProviders(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findUsersByGroupIdAndProfile(groupId, ProfileEnum.SERVICE_PROVIDER);
    }

    @Override
    public Long countPolicyMakers(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndUserProfile(groupId, ProfileEnum.POLICY_MAKER);
    }

    @Override
    public List<User> getPolicyMakers(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findUsersByGroupIdAndProfile(groupId, ProfileEnum.POLICY_MAKER);
    }

    @Override
    public Long countOthers(Integer groupId) {
        return groupUserRelationRepository.countByGroupIdAndUserProfile(groupId, ProfileEnum.OTHER);
    }

    @Override
    public List<User> getOthers(Integer groupId) {
        return (List<User>) groupUserRelationRepository.findUsersByGroupIdAndProfile(groupId, ProfileEnum.OTHER);
    }

    @Override
    public Page<GroupUserRelation> findGroupsByUserIdAndRelation(Integer userId, GroupUserRelationEnum relation, int page, int pageSize) {
        return groupUserRelationRepository.findByUserIdAndRelation(userId, relation, PageRequest.of(page, pageSize));
    }

    @Override
    public List<User> findUsersByGroupId(Integer groupId) {
        return (List<User>)groupUserRelationRepository.findByGroupId(groupId);
    }

    @Override
    public List<User> findUsersByGroupIdAndRelation(Integer groupId, GroupUserRelationEnum relation) {
        return(List<User>)groupUserRelationRepository.findByGroupIdAndRelation(groupId, relation);
    }

    @Override
    public void createNewGroup(Group group, User user) {
        Group newGroup = groupRepository.save(group);
        elasticsearchPublisher.publishGroup(group, ESType.CREATE);
        historyService.createHistory(user, null, newGroup, "history.created_group", HistoryTypeEnum.CREATED_GROUP, VisibleEnum.VISIBLE);
        createGroupUserRelation(newGroup, user, GroupUserRelationEnum.CREATED);
    }

    @Override
    public void createGroupUserRelation(Group group, User user, GroupUserRelationEnum relation) {
        groupUserRelationRepository.save(new GroupUserRelation(group, user, relation));
        historyService.createHistory(user, null, group, "history.joined_group", HistoryTypeEnum.VIEWED_GROUP, VisibleEnum.VISIBLE);
        elasticsearchPublisher.publishUser(user, ESType.UPDATE);
        elasticsearchPublisher.publishGroup(group, ESType.UPDATE);
    }

    @Override
    public void changeRelationWithUser(Group group, User user, GroupUserRelationEnum relation) {
        GroupUserRelation groupUserRelation = groupUserRelationRepository.findByUserAndGroup(user, group);
        groupUserRelation.setRelation(relation);
        groupUserRelationRepository.save(groupUserRelation);
    }

    @Override
    public GroupUserRelation findGroupRelation(Group group, User user) {
        return groupUserRelationRepository.findByUserAndGroup(user, group);
    }

    @Override
    public Page<GroupUserRelation> findGroupRelationsByUser(User user, int page, int pageSize) {
        return groupUserRelationRepository.findByUser(user, PageRequest.of(page, pageSize));
    }

    @Override
    public void deleteGroupUserRelation(GroupUserRelation groupUserRelation) {
        if (groupUserRelation != null) {
            groupUserRelationRepository.delete(groupUserRelation);
            elasticsearchPublisher.publishUser(groupUserRelation.getUser(), ESType.UPDATE);
            elasticsearchPublisher.publishGroup(groupUserRelation.getGroup(), ESType.UPDATE);
        }
    }

    @Override
    public List<Group> findSuggestedGroups(Integer localityId, Integer parentLocalityId, String lang, String secondLang) {
        return groupRepository.findByStatusAndLocalityAndLanguage(GroupStatusEnum.SUGGESTED, localityId, parentLocalityId, lang, secondLang); // TODO add sort?
    }

    @Override
    public List<Group> findAllInProcess(Integer localityId, Integer parentLocalityId, String lang, String secondLang) {
        List<Group> groups = groupRepository.findAllInProcess(localityId, parentLocalityId, lang, secondLang); // TODO add sort?
        for (Group group: groups) {
            List<Post> ideas = postService.findGroupBestIdea(group.getId());
            if (!ideas.isEmpty()) {
                group.setHasIdea(true);
                group.setHasOrganisation(ideas.get(0).getOrganisation() != null);
                group.setHasProposals(postService.findPostsByGroupIdAndPostPhase(group.getId(), PostPhaseEnum.ORG_APPLY).size() != 0);
            } else {
                group.setHasIdea(false);
                group.setHasProposals(false);
                group.setHasOrganisation(false);
            }
        }
        return groups;
    }

    @Override
    public List<Group> findRecentGroups() {
        return groupRepository.findTop4RecentGroups();
    }

    @Override
    public List<Group> findRecentGroupsAnonymously(String language) {
        List<Group> list = groupRepository.findAllByLanguage_Code(language, Constants.SORT_BY_TIMESTAMP_DESC);
        return list.subList(0, list.size() >= 4 ? 4 : list.size());
    }

    @Override
    public List<Group> findRecentGroupsLoggedIn(Integer localityId, Integer localityParentId, String language, String secondLanguage) {
        List<Group> list = groupRepository.findGroupsByLocalityAndLanguageCode(localityId, localityParentId, language, secondLanguage, Constants.SORT_BY_TIMESTAMP_DESC);
        return list.subList(0, list.size() >= 4 ? 4 : list.size());
    }

    /**
     * Find topics
     * **/

    @Override
    public List<Group> findGroupsAnonymously(Optional<String> query, Optional<Integer> sorting, String languageCode) {
        Sort sort = selectSorting(sorting);
        List<Group> groups;
        if (query.isPresent()) {
            String q = "%" + query.get().trim() + "%";
            groups = groupRepository.findAllByLanguageAndQuery(languageCode, q, sort);
            if (groups.isEmpty()) groups = groupRepository.findAllByLanguageAndThemeQuery(languageCode, q, sort);
        } else {
            // TODO change by Recommendation Engine call?
            groups = groupRepository.findAllByLanguage_Code(languageCode, sort);
        }
        return groups;
    }

    @Override
    public List<Group> findGroupsLoggedIn(Optional<String> query, Optional<Integer> sorting, Integer localityId, Integer parentLocalityId, String languageCode, String secondLanguageCode) {
        List<Group> groups;
        Sort sort = selectSorting(sorting);
        if (query.isPresent()) {
            String q = "%" + query.get().trim() + "%";
            groups = groupRepository.findAllByLocalityAndLanguageAndQuery(localityId, parentLocalityId, languageCode, secondLanguageCode, q, sort);
        } else {
            // TODO change by Recommendation Engine call?
            groups = groupRepository.findGroupsByLocalityAndLanguageCode(localityId, parentLocalityId, languageCode, secondLanguageCode, sort);
        }
        return groups;
    }

    private Sort selectSorting(Optional<Integer> sorting) {
        Sort sort = Constants.SORT_BY_TIMESTAMP_DESC; // sort by newests | default
        if (sorting.isPresent()) {
            switch (sorting.get()) {
                /*case 0:
                    sort = Constants.SORT_BY_LIKES_DESC; // more popular
                    break;
                case 1:
                    sort = Constants.SORT_BY_LIKES_ASC; // less popular
                    break;*/
                case 2:
                    sort = Constants.SORT_BY_TIMESTAMP_DESC; // sort by newests
                    break;
                case 3:
                    sort = Constants.SORT_BY_TIMESTAMP_ASC; // sort by oldest
                    break;
            }
        }
        return sort;
    }
}
