package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.util.ApplicationStatusDeSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class BasicApplication {

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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ApplicantType applicantType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("submittedDate")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date submittedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("lastModifiedDate")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date lastModifiedDate;

    @Field("status")
    @JsonDeserialize(using = ApplicationStatusDeSerializer.class)
    private ApplicationStatus status = ApplicationStatus.DRAFT;

    @Field("approver")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String approver;

    @Field("approverId")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long approverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Field("approvedDate")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date approvedDate;

    @Field("approverComments")
    private String approverComments;

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

    public boolean canBeModified() {
        return ApplicationStatus.DRAFT.equals(getStatus());
    }

    public Date modifiedDate(){
        return lastModifiedDate != null ? lastModifiedDate : createdDate;
    }

    public  boolean isSubmitted(){
        return status == ApplicationStatus.SUBMITTED;
    }
}

