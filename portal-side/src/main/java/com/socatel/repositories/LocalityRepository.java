package com.socatel.repositories;

import com.socatel.models.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocalityRepository extends JpaRepository<Locality,Integer> {
    List<Locality> findAllByLocalityParentNotNull();
    @Query("SELECT l FROM Locality l WHERE l.id = 999")
    Locality findPanEuropean();
}
