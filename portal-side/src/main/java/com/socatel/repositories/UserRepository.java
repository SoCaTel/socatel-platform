package com.socatel.repositories;

import com.socatel.models.Organisation;
import com.socatel.models.User;
import com.socatel.utils.enums.ProfileEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    //Optional<User> findByConfirmationToken(String confirmationToken);
    @Query("SELECT u FROM User u WHERE u.role = 1")
    List<User> findModerators();
    @Query("SELECT u FROM User u WHERE u.enabled = 2")
    List<User> findBannedUsers(Sort sort);
    @Query("SELECT u FROM User u WHERE u.anonymized = 0" +
            "AND u.organisation = :organisation")
    List<User> findByOrganisation(Organisation organisation);
    @Query("SELECT u FROM User u WHERE u.anonymized = 0")
    List<User> findAllByAnonymizedIsFalse();
    @Query("SELECT u.username FROM User u " +
            "WHERE u.anonymized = 0 " +
            "AND u.username LIKE :username " +
            "AND u.username <> :loggedUsername")
    List<?> findByUsernameLike(String username, String loggedUsername);

    /**
     * Updates
     */
    @Modifying
    @Query("UPDATE User u SET u.profile = :p WHERE u.id = :userId")
    void updateProfile(@Param("userId") Integer userId, @Param("p") ProfileEnum profileEnum);
}
