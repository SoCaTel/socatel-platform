package com.socatel.models;

import com.socatel.utils.enums.InvitationStatus;
import com.socatel.utils.enums.OrgRoleEnum;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "so_invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private int id;

    @Column(name = "invitation_token")
    private String token;

    @Column(name = "invitation_time")
    private Timestamp timestamp;

    @Column(name = "invitation_email")
    private String email;

    @Column(name = "invitation_role")
    private OrgRoleEnum role;

    @Column(name = "invitation_status")
    private InvitationStatus status;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="organisation_id")
    private Organisation organisation;

    public Invitation() {
        timestamp = new Timestamp(System.currentTimeMillis());
        this.status = InvitationStatus.SENT;
    }

    public Invitation(String token, String email, User user, Organisation organisation, OrgRoleEnum role) {
        timestamp = new Timestamp(System.currentTimeMillis());
        this.token = token;
        this.email = email;
        this.user = user;
        this.organisation = organisation;
        this.status = InvitationStatus.SENT;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public OrgRoleEnum getRole() {
        return role;
    }

    public void setRole(OrgRoleEnum role) {
        this.role = role;
    }
}
