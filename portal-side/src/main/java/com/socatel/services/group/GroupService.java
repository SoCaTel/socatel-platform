package com.socatel.services.group;

import com.socatel.models.Group;
import com.socatel.models.User;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.utils.enums.GroupUserRelationEnum;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface GroupService {
    List<Group> findAll();

    Group findByName(String groupName);
    Long countAll();
    Group findById(Integer id);
    Long countSubscribers(Integer groupId);

    Long countContributors(Integer groupId);

    List<User> getContributors(Integer groupId);

    Long countOlderPeople(Integer groupId);

    List<User> getOlderPeople(Integer groupId);

    Long countFamilyFriends(Integer groupId);

    List<User> getFamilyFriends(Integer groupId);

    Long countCareAssistants(Integer groupId);

    List<User> getCareAssistants(Integer groupId);

    Long countHealthcareSpecialists(Integer groupId);

    List<User> getHealthcareSpecialists(Integer groupId);

    Long countServiceProviders(Integer groupId);

    List<User> getServiceProviders(Integer groupId);

    Long countPolicyMakers(Integer groupId);

    List<User> getPolicyMakers(Integer groupId);

    Long countOthers(Integer groupId);
    void createNewGroup(Group group, User user);
    void createGroupUserRelation(Group group, User user, GroupUserRelationEnum relation);
    GroupUserRelation findGroupRelation(Group group, User user);
    void changeRelationWithUser(Group group, User user, GroupUserRelationEnum relation);
    List<User> findUsersByGroupId(Integer groupId);
    List<User> findUsersByGroupIdAndRelation(Integer groupId, GroupUserRelationEnum relation);
    Page<GroupUserRelation> findGroupRelationsByUser(User user, int page, int pageSize);

    List<User> getOthers(Integer groupId);

    Page<GroupUserRelation> findGroupsByUserIdAndRelation(Integer userId, GroupUserRelationEnum relation, int page, int pageSize);
    void save(Group group);
    void deleteGroupUserRelation(GroupUserRelation groupUserRelation);
    List<Group> findSuggestedGroups(Integer localityId, Integer parentLocalityId, String lang, String secondLang);
    List<Group> findAllInProcess(Integer localityId, Integer parentLocalityId, String lang, String secondLang);
    List<Group> findRecentGroups();
    List<Group> findRecentGroupsAnonymously(String language);
    List<Group> findRecentGroupsLoggedIn(Integer localityId, Integer localityParentId, String language, String secondLanguage);

    /**
     * Find topics
     * **/
    List<Group> findGroupsAnonymously(Optional<String> query, Optional<Integer> sorting, String languageCode);
    List<Group> findGroupsLoggedIn(Optional<String> query, Optional<Integer> sorting, Integer localityId, Integer parentLocalityId, String languageCode, String secondLanguageCode);
}
