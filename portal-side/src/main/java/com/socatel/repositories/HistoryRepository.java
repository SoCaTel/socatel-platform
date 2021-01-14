package com.socatel.repositories;

import com.socatel.models.History;
import com.socatel.models.User;
import com.socatel.utils.enums.VisibleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History,Integer> {
    Page<History> findByUserAndLevel(User user, VisibleEnum level, Pageable pageable);
    void deleteAllByUser(User user);
}
