package com.ispirit.digitalsky.domain;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BasicApplicationForm {

    @Id
    private String id;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date submittedDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastModifiedDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date approvedDate;
    private String approverComments;
    private String applicationNumber;
    private ApplicationStatus status;
    private String approvedById;
    private String applicant;
    private Address applicantAddress;
    private String applicantEmail;
    private String applicantPhone;
    private String applicantNationality;
    private ApplicantType applicantType;

    public String getId() {
        return id;
    }

    public String getApproverComments() {
        return approverComments;
    }

    public void setApproverComments(String approverComments) {
        this.approverComments = approverComments;
    }

    public String getEmail() {
        return applicantEmail;
    }

    public void setEmail(String email) {
        this.applicantEmail = email;
    }

    public String getPhone() {
        return applicantPhone;
    }

    public void setPhone(String phone) {
        this.applicantPhone = phone;
    }

    public String getNationality() {
        return applicantNationality;
    }

    public void setNationality(String nationality) {
        this.applicantNationality = nationality;
    }

    public String getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(String approvedById) {
        this.approvedById = approvedById;
    }

    public Address getAddress() {
        return applicantAddress;
    }

    public void setAddress(Address address) {
        this.applicantAddress = address;
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

    public String approvedById() {
        return approvedById;
    }

   public void approvedById(String approvedById) {
       this.approvedById = approvedById;
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

