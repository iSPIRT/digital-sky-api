package com.ispirit.digitalsky.domain;

import java.util.Date;

public class ApproveRequestBody {

    private String _id;
    private ApplicationStatus status;
    private String approvedById;
    private String comments;
    private static final Date approvedDate = new Date();

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
