package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ispirit.digitalsky.document.AddressDocument;
import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BasicApplicationForm {

    @Id
    @Field("id")
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("createdDate")
    private Date createdDate;

    @Field("applicationNumber")
    private String applicationNumber;

    @Field("applicant")
    private String applicant;

    @Field("applicantId")
    private long applicantId;

    @Field("applicantAddress")
    private AddressDocument applicantAddress;

    @Field("applicantEmail")
    private String applicantEmail;

    @Field("applicantPhone")
    private String applicantPhone;

    @Field("applicantNationality")
    private String applicantNationality;

    @Field("applicantType")
    private ApplicantType applicantType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("submittedDate")
    private Date submittedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("lastModifiedDate")
    private Date lastModifiedDate;

    @Field("status")
    private ApplicationStatus status;

    @Field("approver")
    private String approver;

    @Field("approverId")
    private long approverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("approvedDate")
    private Date approvedDate;

    @Field("approverComments")
    private String approverComments;

    public String getId() {
        return id;
    }

    public String getApproverComments() {
        return approverComments;
    }

    public void setApproverComments(String approverComments) {
        this.approverComments = approverComments;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getApplicantNationality() {
        return applicantNationality;
    }

    public void setApplicantNationality(String applicantNationality) { this.applicantNationality = applicantNationality; }

    public AddressDocument getApplicantAddress() {
        return applicantAddress;
    }

    public Date getCreatedDate() { return createdDate; }

    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public long getApplicantId() { return applicantId; }

    public void setApplicantId(long applicantId) { this.applicantId = applicantId; }

    public String getApprover() { return approver; }

    public void setApprover(String approver) { this.approver = approver; }

    public long getApproverId() { return approverId; }

    public void setApproverId(long approverId) {  this.approverId = approverId; }

    public void setApplicantAddress(AddressDocument applicantAddress) {
        this.applicantAddress = applicantAddress;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
   }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public ApplicantType getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(ApplicantType applicantType) {
        this.applicantType = applicantType;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}

