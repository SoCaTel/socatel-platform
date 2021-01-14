package com.socatel.repositories;

import com.socatel.dtos.knowledge_base.SkillIdDTO;
import com.socatel.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill,Integer> {
    @Query("SELECT new com.socatel.dtos.knowledge_base.SkillIdDTO(s.id, s.name) FROM Skill s " +
            "LEFT JOIN s.users u WHERE u.id = :id")
    List<SkillIdDTO> findSkillsByUserId(Integer id);
}
