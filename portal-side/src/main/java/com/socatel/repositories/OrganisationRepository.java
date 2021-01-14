package com.socatel.repositories;

import com.socatel.models.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation,Integer> {
    Optional<Organisation> findByName(String name);
}
