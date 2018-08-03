package com.ispirit.digitalsky.domain;

import javax.validation.constraints.NotNull;

public class ApproveRequestBody {

    @NotNull
    private String applicationFormId;

    @NotNull
    private ApplicationStatus status;

    @NotNull
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
