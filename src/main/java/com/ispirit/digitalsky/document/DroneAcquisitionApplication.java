package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ispirit.digitalsky.domain.ApplicantCategory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public abstract class DroneAcquisitionApplication extends BasicApplication {

    @Field("manufacturer")
    protected String manufacturer;

    @Field("manufacturerId")
    protected long manufacturerId;

    @Field("manufacturerAddress")
    protected AddressDocument manufacturerAddress;

    @Field("manufacturerNationality")
    protected String manufacturerNationality;

    @Field("droneTypeId")
    protected long droneTypeId;

    @Field("owner")
    protected String owner;

    @Field("ownerId")
    protected long ownerId;

    @Field("ownerAddress")
    protected AddressDocument ownerAddress;

    @Field("modelName")
    protected String modelName;

    @Field("modelNo")
    protected String modelNo;

    @Field("serialNo")
    protected String serialNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    @Field("dateOfManufacture")
    protected Date dateOfManufacture;

    @Field("yearOfManufacture")
    protected String yearOfManufacture;

    @Field("wingType")
    protected String wingType;

    @Field("noOfDrones")
    protected int noOfDrones;

    @Field("isNew")
    protected Boolean isNew;

    @Field("maxTakeOffWeight")
    protected float maxTakeOffWeight;

    @Field("maxHeightAttainable")
    protected float maxHeightAttainable;

    @Field("compatiblePayload")
    protected String compatiblePayload;

    @Field("purposeOfOperation")
    protected String purposeOfOperation;

    @Field("proposedBaseOfOperation")
    protected String proposedBaseOfOperation;

    @Field("securityClearanceDocument")
    protected String securityClearanceDocName;

    @JsonIgnore
    @Transient
    protected MultipartFile securityClearanceDoc;

    public DroneAcquisitionApplication() { setCreatedDate(new Date());}

    public ApplicantCategory getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(ApplicantCategory applicantCategory) { this.applicantCategory = applicantCategory; }

    private ApplicantCategory applicantCategory;

    public String getManufacturer() {
        return manufacturer;
    }

    public long getDroneTypeId() { return droneTypeId; }

    public void setDroneTypeId(long droneTypeId) { this.droneTypeId = droneTypeId; }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public AddressDocument getManufacturerAddress() {
        return manufacturerAddress;
    }

    public void setManufacturerAddress(AddressDocument manufacturerAddress) { this.manufacturerAddress = manufacturerAddress; }

    public String getModelName() { return modelName; }

    public void setModelName(String modelName) { this.modelName = modelName; }

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

    public String getcompatiblePayload() {
        return compatiblePayload;
    }

    public void setcompatiblePayload(String compatiblePayload) {
        this.compatiblePayload = compatiblePayload;
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

    public String getSecurityClearanceDocName() { return securityClearanceDocName; }

    public void setSecurityClearanceDocName(String securityClearanceDocName) { this.securityClearanceDocName = securityClearanceDocName; }

    public MultipartFile getSecurityClearanceDoc() { return securityClearanceDoc; }

    public void setSecurityClearanceDoc(MultipartFile securityClearanceDoc) { this.securityClearanceDoc = securityClearanceDoc; }

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
