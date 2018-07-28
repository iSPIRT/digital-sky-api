package com.ispirit.digitalsky.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.document.BasicApplication;
import com.ispirit.digitalsky.util.CustomDateSerializer;

import java.util.Date;


public class ApplicationAbstract {

    private String id;

    private String type;

    private String status;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createdDate;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updatedDate;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date submittedDate;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date approvalDateDate;

    public ApplicationAbstract(BasicApplication application) {
        this.id = application.getId();
        this.type = application.getClass().getSimpleName();
        this.status = application.getStatus().name();
        this.createdDate =application.getCreatedDate();
        this.updatedDate =application.modifiedDate();
        this.submittedDate =application.getSubmittedDate();
        this.approvalDateDate =application.getApprovedDate();
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public Date getApprovalDateDate() {
        return approvalDateDate;
    }
}
