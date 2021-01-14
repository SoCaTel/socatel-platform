package com.socatel.dtos;

import com.socatel.models.Report;
import com.socatel.models.User;

import java.util.List;

public class UserReports {
    private List<Report> reports;
    private User reported;

    public UserReports() {

    }

    public UserReports(User user, List<Report> reports) {
        this.reported = user;
        this.reports = reports;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public User getReported() {
        return reported;
    }

    public void setReported(User reported) {
        this.reported = reported;
    }
}
