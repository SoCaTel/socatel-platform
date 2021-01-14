package com.socatel.services.report;

import com.socatel.models.Post;
import com.socatel.models.Proposition;
import com.socatel.models.Report;
import com.socatel.models.User;
import com.socatel.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Page<Report> findAll(int pageSize) {
        return reportRepository.findAllDistinct(PageRequest.of(0, pageSize));
    }

    @Override
    public List<Report> findAllPending() {
        return reportRepository.findAllPending();
    }

    @Override
    public void save(Report report) {
        reportRepository.save(report);
    }

    @Override
    public List<User> findAllReporteds() {
        return reportRepository.findAllReporteds();
    }

    @Override
    public void createReport(Report report) {
        reportRepository.save(report);
    }

    @Override
    public Report findById(Integer id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Override
    public List<Report> findByReported(User reported) {
        return reportRepository.findByReported(reported);
    }

    @Override
    public void deleteByPost(Post post) {
        reportRepository.deleteByPost(post);
    }

    @Override
    public void deleteByProposition(Proposition proposition) {
        reportRepository.deleteByProposition(proposition);
    }

    @Override
    public void deleteAll(List<Report> reports) {
        reportRepository.deleteAll(reports);
    }

    @Override
    public void saveAll(List<Report> reports) {
        reportRepository.saveAll(reports);
    }

    @Override
    public List<Report> findByPostId(int postId) {
        return reportRepository.findByPostId(postId);
    }

    @Override
    public List<Report> findByPropositionId(int propositionId) {
        return reportRepository.findByPropositionId(propositionId);
    }
}
