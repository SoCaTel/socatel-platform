package com.socatel.models.tokens;

import com.socatel.models.User;
import com.socatel.utils.Constants;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "so_verification_token")
public class VerificationToken {

    @Id
    @Column(name = "verification_token_id")
    private int id;

    @Column(name = "verification_token_token")
    private String token;

    @Column(name = "verification_token_expiry_date")
    private Date expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, Constants.VERIFICATION_TOKEN_EXPIRATION_MINUTES);
        return new Date(cal.getTime().getTime());
    }

    public VerificationToken() {}

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate();
    }

    public void generateNewToken() {
        token = UUID.randomUUID().toString();
        expiryDate = calculateExpiryDate();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}