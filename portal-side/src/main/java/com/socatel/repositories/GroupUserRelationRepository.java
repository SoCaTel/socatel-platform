package com.socatel.repositories;

import com.socatel.models.Group;
import com.socatel.models.User;
import com.socatel.models.relationships.GroupUserRelation;
import com.socatel.utils.enums.GroupUserRelationEnum;
import com.socatel.utils.enums.ProfileEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupUserRelationRepository extends JpaRepository<GroupUserRelation, Integer> {
    @Query("SELECT r FROM GroupUserRelation r " +
            "WHERE r.userId = :userId " +
            "AND r.relation = :relation " +
            "AND r.group.status not in (0, 6)")
    Page<GroupUserRelation> findByUserIdAndRelation(Integer userId, GroupUserRelationEnum relation, Pageable pageable);
    @Query("SELECT r.user FROM GroupUserRelation r " +
            "WHERE r.groupId = :groupId " +
            "AND r.relation = :relation")
    List<?> findByGroupIdAndRelation(Integer groupId, GroupUserRelationEnum relation);
    @Query("SELECT r.user FROM GroupUserRelation r " +
            "WHERE r.groupId = :groupId ")
    List<?> findByGroupId(Integer groupId);
    GroupUserRelation findByUserAndGroup(User user, Group group);
    @Query("SELECT r FROM GroupUserRelation r " +
            "WHERE r.user = :user " +
            "AND r.group.status not in (0, 6)")
    Page<GroupUserRelation> findByUser(User user, Pageable pageable);
    Long countByGroupId(Integer groupId);
    Long countByGroupIdAndRelation(int groupId, GroupUserRelationEnum relation);
    @Query("SELECT COUNT (r) FROM GroupUserRelation r " +
            "WHERE r.groupId = :groupId " +
            "AND r.user.profile = :user_profile " +
            "AND r.relation not in (0)")
    Long countByGroupIdAndUserProfile(Integer groupId, ProfileEnum user_profile);
    @Query("SELECT r.user FROM GroupUserRelation r " +
            "WHERE r.groupId = :groupId " +
            "AND r.user.profile = :user_profile " +
            "AND r.relation not in (0)")
    List<?> findUsersByGroupIdAndProfile(Integer groupId, ProfileEnum user_profile);
    @Query("SELECT r.user FROM GroupUserRelation r " +
            "WHERE r.groupId = :groupId " +
            "AND r.relation not in (0)")
    List<?> findContributorsInGroup(Integer groupId);
}
