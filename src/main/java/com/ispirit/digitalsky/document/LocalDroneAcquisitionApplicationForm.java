package com.ispirit.digitalsky.document;

import com.ispirit.digitalsky.domain.*;
import org.springframework.data.annotation.Transient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

public class LocalDroneAcquisitionApplicationForm extends BasicApplicationForm {

    private String manufacturer;
    private AddressDocument manufacturerAddress;
    private String modelNo;
    private String serialNo;
    private Date dateOfManufacture;
    private String yearOfManufacture;
    private Boolean fixedWing;
    private Boolean isNew;
    private float maxTakeOffWeight;
    private float maxHeightAttainable;
    private String payloadDetails;
    private String purposeOfOperation;
    private String proposedBaseOfOperation;
    private ModeOfAcquisition acquisitionMode;
    private List<DroneType> dronesToBeAcquired;
    @Transient
    private MultipartFile securityClearanceDoc;
    @Transient
    private MultipartFile etaClearancedoc;

    public ModeOfAcquisition getAcquisitionMode() {
        return acquisitionMode;
    }

    public void setAcquisitionMode(ModeOfAcquisition acquisitionMode) {
        this.acquisitionMode = acquisitionMode;
    }

    public ApplicantCategory getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(ApplicantCategory applicantCategory) {
        this.applicantCategory = applicantCategory;
    }

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

    public void setManufacturerAddress(AddressDocument manufacturerAddress) {
        this.manufacturerAddress = manufacturerAddress;
    }

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

    public Boolean getFixedWing() {
        return this.fixedWing;
    }

    public void setFixedWing(Boolean fixedWing) {
        this.fixedWing = fixedWing;
    }

    public Boolean getNew() {
        return this.isNew;
    }

    public void setNew(Boolean isNew) {
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

    public void setProposedBaseOfOperation(String proposedBaseOfOperation) {
        this.proposedBaseOfOperation = proposedBaseOfOperation;
    }

    public MultipartFile getSecurityClearanceDoc() {
        return securityClearanceDoc;
    }

    public void setSecurityClearanceDoc(MultipartFile securityClearanceDoc) {
        this.securityClearanceDoc = securityClearanceDoc;
    }

    public MultipartFile getEtaClearancedoc() {
        return etaClearancedoc;
    }

    public void setEtaClearancedoc(MultipartFile etaClearancedoc) {
        this.etaClearancedoc = etaClearancedoc;
    }

    public List<DroneType> getDronesToBeAcquired() {
        return dronesToBeAcquired;
    }

    public void setDronesToBeAcquired(List<DroneType> dronesToBeAcquired) {
        this.dronesToBeAcquired = dronesToBeAcquired;
    }



//    public void merge(LocalDroneAcquisitionForm acquisitionForm) {
//        System.out.println("Inside Merge");
//        Field[] fields = this.getClass().getFields();
//        for( Field field : fields){
//            Object newValue = null;
//            try {
//                System.out.println("Inside Merge try catch");
//                newValue = field.get(acquisitionForm);
//                if(newValue != null) {
//                    System.out.println(String.format("field - %s; newvalue - %s",field.getName(),newValue ));
//                    field.set(this,newValue);
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
