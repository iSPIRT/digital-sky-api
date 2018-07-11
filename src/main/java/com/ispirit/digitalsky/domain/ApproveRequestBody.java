package com.ispirit.digitalsky.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ApproveRequestBody {

    private String id;
    private ApplicationStatus status;
    private String approvedById;
    private String comments;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private static final Date approvedDate = new Date();

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(String approvedById) {
        this.approvedById = approvedById;
    }

    public static Date getApprovedDate() {
        return approvedDate;
    }
}
