package com.socatel.repositories;

import com.socatel.models.Post;
import com.socatel.models.Proposition;
import com.socatel.models.Report;
import com.socatel.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report,Integer> {
    @Query("SELECT r FROM Report r WHERE r.status = 0 GROUP BY r.post.id, r.proposition.id")
    Page<Report> findAllDistinct(Pageable pageable);
    @Query("SELECT r FROM Report r WHERE r.status = 0")
    List<Report> findAllPending();
    @Query("SELECT r.reported FROM Report r WHERE r.status = 0 AND r.reported.anonymized = 0 GROUP BY r.reported")
    List<User> findAllReporteds();
    @Query("SELECT r FROM Report r WHERE r.status = 0 AND r.reported.anonymized = 0 AND r.reported = :reported " +
            "GROUP BY r.post.id, r.proposition.id")
    List<Report> findByReported(User reported);
    List<Report> findByPostId(int post_id);
    List<Report> findByPropositionId(int proposition_id);
    void deleteByPost(Post post);
    void deleteByProposition(Proposition proposition);
}
