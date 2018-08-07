package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ispirit.digitalsky.domain.PersonType;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public abstract class BasicApplication {

    @Id
    @Field("id")
    protected String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("createdDate")
    protected Date createdDate;

    @Field("applicationNumber")
    protected String applicationNumber;

    @Field("applicant")
    protected String applicant;

    @Field("applicantId")
    protected long applicantId;

    @Field("applicantAddress")
    protected AddressDocument applicantAddress;

    @Field("applicantEmail")
    protected String applicantEmail;

    @Field("applicantPhone")
    protected String applicantPhone;

    @Field("applicantNationality")
    protected String applicantNationality;

    @Field("applicantType")
    protected PersonType applicantType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("submittedDate")
    protected Date submittedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("lastModifiedDate")
    protected Date lastModifiedDate;

    @Field("status")
    protected ApplicationStatus status = ApplicationStatus.DRAFT;

    @Field("approver")
    protected String approver;

    @Field("approverId")
    protected long approverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("approvedDate")
    protected Date approvedDate;

    @Field("approverComments")
    protected String approverComments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public PersonType getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(PersonType applicantType) {
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

    public boolean canBeModified() {
        return ApplicationStatus.DRAFT.equals(getStatus());
    }

    public Date modifiedDate(){
        return lastModifiedDate != null ? lastModifiedDate : createdDate;
    }
}

