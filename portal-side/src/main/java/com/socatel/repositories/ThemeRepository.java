package com.socatel.repositories;

import com.socatel.dtos.knowledge_base.ThemeIdDTO;
import com.socatel.models.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ThemeRepository extends JpaRepository<Theme,Integer> {
    @Query("SELECT new com.socatel.dtos.knowledge_base.ThemeIdDTO(t.id, t.name) FROM Theme t " +
            "LEFT JOIN t.users u WHERE u.id = :id")
    List<ThemeIdDTO> findThemesByUserId(Integer id);

    @Query("SELECT new com.socatel.dtos.knowledge_base.ThemeIdDTO(t.id, t.name) FROM Theme t " +
            "LEFT JOIN t.groups g WHERE g.id = :id")
    List<ThemeIdDTO> findThemesByGroupId(Integer id);

    @Query("SELECT new com.socatel.dtos.knowledge_base.ThemeIdDTO(t.id, t.name) FROM Theme t " +
            "LEFT JOIN t.services s WHERE s.id = :id")
    List<ThemeIdDTO> findThemesByServiceId(Integer id);

    @Query("SELECT t FROM Theme t " +
            "LEFT JOIN t.groups g WHERE g.id = :id")
    Set<Theme> findGroupThemes(Integer id);
}
