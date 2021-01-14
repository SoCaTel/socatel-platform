package com.socatel.services.user;

import com.socatel.models.Document;
import com.socatel.models.Organisation;
import com.socatel.models.User;
import com.socatel.models.tokens.PasswordResetToken;
import com.socatel.models.tokens.VerificationToken;

import java.util.List;

public interface UserService {
    // User findById(Integer id);
    Long countAll();
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAllNotAnonymized();
    User findById(Integer id);
    List<?> findByUsernameLike(String username, String loggedUsername);
    void setProfilePicture(User user, Document document);
    void save(User user);
    List<User> findAll();
    List<User> getBannedUsers();
    void anonymise(User user);
    void delete(User user);
    List<User> getModerators();
    User getUser(String verificationToken);
    List<User> getOrganisationMembers(Organisation organisation);
    void createVerificationToken(User user, String token);
    VerificationToken getVerificationToken(String verificationToken);
    VerificationToken generateNewVerificationToken(String verificationToken);
    void createPasswordResetToken(User user, String token);
    PasswordResetToken getPasswordResetToken(String token);
    PasswordResetToken getPasswordResetToken(String token, Integer userId);
}
