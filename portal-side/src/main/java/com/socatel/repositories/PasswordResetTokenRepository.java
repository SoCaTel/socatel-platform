package com.socatel.repositories;

import com.socatel.models.tokens.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByTokenAndUserId(String token, Integer userId);
}
