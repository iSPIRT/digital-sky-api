package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ispirit.digitalsky.domain.ApplicantCategory;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class DroneAcquisitionApplication extends BasicApplication {

    @Field("manufacturer")
    private String manufacturer;

    @Field("manufacturerId")
    private long manufacturerId;

    @Field("manufacturerAddress")
    private AddressDocument manufacturerAddress;

    @Field("manufacturerNationality")
    private String manufacturerNationality;

    @Field("owner")
    private String owner;

    @Field("ownerId")
    private long ownerId;

    @Field("ownerAddress")
    private AddressDocument ownerAddress;

    @Field("modelNo")
    private String modelNo;

    @Field("serialNo")
    private String serialNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    @Field("dateOfManufacture")
    private Date dateOfManufacture;

    @Field("yearOfManufacture")
    private String yearOfManufacture;

    @Field("wingType")
    private String wingType;

    @Field("noOfDrones")
    private int noOfDrones;

    @Field("isNew")
    private Boolean isNew;

    @Field("maxTakeOffWeight")
    private float maxTakeOffWeight;

    @Field("maxHeightAttainable")
    private float maxHeightAttainable;

    @Field("payloadDetails")
    private String payloadDetails;

    @Field("purposeOfOperation")
    private String purposeOfOperation;

    @Field("proposedBaseOfOperation")
    private String proposedBaseOfOperation;

    @Field("securityClearanceDocument")
    private String securityClearanceDoc;

    public DroneAcquisitionApplication() { setCreatedDate(new Date());}

    public ApplicantCategory getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(ApplicantCategory applicantCategory) { this.applicantCategory = applicantCategory; }

    private ApplicantCategory applicantCategory;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public AddressDocument getManufacturerAddress() {
        return manufacturerAddress;
    }

    public void setManufacturerAddress(AddressDocument manufacturerAddress) { this.manufacturerAddress = manufacturerAddress; }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Date getDateOfManufacture() {
        return dateOfManufacture;
    }

    public void setDateOfManufacture(Date dateOfManufacture) {
        this.dateOfManufacture = dateOfManufacture;
    }

    public String getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(String yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getWingType() {
        return this.wingType;
    }

    public void setWingType(String wingType) {
        this.wingType = wingType;
    }

    public Boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public float getMaxTakeOffWeight() {
        return maxTakeOffWeight;
    }

    public void setMaxTakeOffWeight(float maxTakeOffWeight) {
        this.maxTakeOffWeight = maxTakeOffWeight;
    }

    public float getMaxHeightAttainable() {
        return maxHeightAttainable;
    }

    public void setMaxHeightAttainable(float maxHeightAttainable) {
        this.maxHeightAttainable = maxHeightAttainable;
    }

    public String getPayloadDetails() {
        return payloadDetails;
    }

    public void setPayloadDetails(String payloadDetails) {
        this.payloadDetails = payloadDetails;
    }

    public String getPurposeOfOperation() {
        return purposeOfOperation;
    }

    public void setPurposeOfOperation(String purposeOfOperation) {
        this.purposeOfOperation = purposeOfOperation;
    }

    public String getProposedBaseOfOperation() {
        return proposedBaseOfOperation;
    }

    public void setProposedBaseOfOperation(String proposedBaseOfOperation) { this.proposedBaseOfOperation = proposedBaseOfOperation; }

    public String getSecurityClearanceDoc() { return securityClearanceDoc; }

    public void setSecurityClearanceDoc(String securityClearanceDoc) { this.securityClearanceDoc = securityClearanceDoc; }

    public int getNoOfDrones() {
        return noOfDrones;
    }

    public void setNoOfDrones(int noOfDrones) {
        this.noOfDrones = noOfDrones;
    }

    public AddressDocument getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(AddressDocument ownerAddress) { this.ownerAddress = ownerAddress; }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public long getManufacturerId() { return manufacturerId; }

    public void setManufacturerId(long manufacturerId) { this.manufacturerId = manufacturerId; }

    public long getOwnerId() { return ownerId; }

    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }

    public String getManufacturerNationality() { return manufacturerNationality; }

    public void setManufacturerNationality(String manufacturerNationality) { this.manufacturerNationality = manufacturerNationality; }
}
