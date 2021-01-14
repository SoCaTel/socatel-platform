package com.socatel.repositories;

import com.socatel.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language,Integer> {
    Optional<Language> findByCode(String code);
    @Query("select l.code from Language l")
    List<?> findAllCodes();
}
