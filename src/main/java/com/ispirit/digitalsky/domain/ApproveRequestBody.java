package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ApproveRequestBody {

    private String applicationFormId;
    private ApplicationStatus status;
    private String comments;

    public String getApplicationFormId() { return applicationFormId; }

    public void setApplicationFormId(String applicationFormId) { this.applicationFormId = applicationFormId; }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
