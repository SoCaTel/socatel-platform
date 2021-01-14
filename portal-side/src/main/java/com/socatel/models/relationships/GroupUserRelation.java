package com.socatel.models.relationships;

import com.socatel.models.Group;
import com.socatel.models.User;
import com.socatel.models.keys.GroupUserKey;
import com.socatel.utils.enums.GroupUserRelationEnum;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "so_group_user")
@IdClass(GroupUserKey.class)
public class GroupUserRelation implements Serializable {

    @Id
    @Column(name = "group_id")
    private int groupId;

    @Id
    @Column(name = "user_id")
    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("group_id")
    @JoinColumn(name="group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "group_user_relation")
    private GroupUserRelationEnum relation;

    public GroupUserRelation() {}

    public GroupUserRelation(Group group, User user, GroupUserRelationEnum relation) {
        this.userId = user.getId();
        this.groupId = group.getId();
        this.group = group;
        this.user = user;
        this.relation = relation;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GroupUserRelationEnum getRelation() {
        return relation;
    }

    public void setRelation(GroupUserRelationEnum relation) {
        this.relation = relation;
    }
}
