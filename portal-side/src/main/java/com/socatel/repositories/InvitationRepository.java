package com.socatel.repositories;

import com.socatel.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation,Integer> {
    Optional<Invitation> findByToken(String token);
}
