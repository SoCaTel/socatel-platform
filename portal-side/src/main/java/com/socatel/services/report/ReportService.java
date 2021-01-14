package com.socatel.services.report;

import com.socatel.models.Post;
import com.socatel.models.Proposition;
import com.socatel.models.Report;
import com.socatel.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReportService {
    Page<Report> findAll(int pageSize);

    List<Report> findAllPending();

    void save(Report report);

    List<User> findAllReporteds();

    void createReport(Report report);
    Report findById(Integer id);
    List<Report> findByReported(User reported);
    void deleteByPost(Post post);
    void deleteByProposition(Proposition proposition);
    void deleteAll(List<Report> reports);
    void saveAll(List<Report> reports);
    List<Report> findByPostId(int postId);
    List<Report> findByPropositionId(int propositionId);
}
