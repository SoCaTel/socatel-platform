package com.socatel.repositories;

import com.socatel.models.Group;
import com.socatel.models.Question;
import com.socatel.utils.enums.QuestionEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Integer> {
    List<Question> findAllByGroupAndType(Group group, QuestionEnum type);
    Long countByGroup(Group group);
}
