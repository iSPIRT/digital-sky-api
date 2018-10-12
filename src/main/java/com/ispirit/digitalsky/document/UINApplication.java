package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.domain.DroneDimensions;
import com.ispirit.digitalsky.domain.DroneCategoryType;
import com.ispirit.digitalsky.util.CustomLocalDateDeSerializer;
import com.ispirit.digitalsky.util.CustomLocalDateSerializer;
import com.ispirit.digitalsky.util.LocalDateAttributeConverter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Convert;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "uinApplications")
@TypeAlias("uinApplication")
public class UINApplication extends BasicApplication {

    @Field("importPermissionDocName")
    private String importPermissionDocName;

    @JsonIgnore
    @Transient
    private MultipartFile importPermissionDoc;

    @Field("cinDocName")
    private String cinDocName;

    @JsonIgnore
    @Transient
    private MultipartFile cinDoc;

    @Field("gstinDocName")
    private String gstinDocName;

    @JsonIgnore
    @Transient
    private MultipartFile gstinDoc;

    @Field("panCardDocName")
    private String panCardDocName;

    @JsonIgnore
    @Transient
    private MultipartFile panCardDoc;

    @Field("securityClearanceDocName")
    private String securityClearanceDocName;

    @JsonIgnore
    @Transient
    private MultipartFile securityClearanceDoc;

    @Field("dotPermissionDocName")
    private String dotPermissionDocName;

    @JsonIgnore
    @Transient
    private MultipartFile dotPermissionDoc;

    @Field("etaDocName")
    private String etaDocName;

    @JsonIgnore
    @Transient
    private MultipartFile etaDoc;

    @Field("feeDetails")
    @NotNull
    private String feeDetails;

    @Field("droneTypeId")
    private long droneTypeId;

    @Field("operatorDroneId")
    private long operatorDroneId;

    @Field("operatorId")
    private long operatorId;

    @Field("uniqueDeviceId")
    private String uniqueDeviceId;

    @Field("manufacturer")
    private String manufacturer;

    @Field("manufacturerId")
    private long manufacturerId;

    @Field("manufacturerAddress")
    private AddressDocument manufacturerAddress;

    @Field("manufacturerNationality")
    private String manufacturerNationality;

    @Field("modelName")
    private String modelName;

    @Field("modelNo")
    private String modelNo;

    @Field("serialNo")
    private String serialNo;

    @Field("dateOfManufacture")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate dateOfManufacture;

    @Field("wingType")
    private String wingType;

    @Field("isNew")
    private Boolean isNew = true;

    @Field("maxTakeOffWeight")
    private float maxTakeOffWeight;

    @Field("maxHeightAttainable")
    private float maxHeightAttainable;

    @Field("compatiblePayload")
    private String compatiblePayload;

    @Field("droneCategoryType")
    private DroneCategoryType droneCategoryType;

    @Field("regionOfOperation")
    private String regionOfOperation;

    @Field("purposeOfOperation")
    private String purposeOfOperation;

    @Field("engineType")
    private String engineType;

    @Field("enginePower")
    private float enginePower;

    @Field("engineCount")
    private int engineCount;

    @Field("fuelCapacity")
    private float fuelCapacity;

    @Field("propellerDetails")
    private String propellerDetails;

    @Field("dimensions")
    private DroneDimensions dimensions;

    @Field("maxEndurance")
    private int maxEndurance;

    @Field("maxRange")
    private float maxRange;

    @Field("maxSpeed")
    private float maxSpeed;

    @Field("maxHeightOfOperation")
    private float maxHeightOfOperation;

    @Field("hasGNSS")
    private boolean hasGNSS;

    @Field("hasAutonomousFlightTerminationSystem")
    private boolean hasAutonomousFlightTerminationSystem;

    @Field("hasFlashingAntiCollisionStrobeLights")
    private boolean hasFlashingAntiCollisionStrobeLights;

    @Field("hasRFID_GSM_SIMCard")
    private boolean hasRFID_GSM_SIMCard;

    @Field("hasFlightController")
    private boolean hasFlightController;

    @Field("previousUIN")
    private String previousUIN;

    @Field("opManualDocName")
    private String opManualDocName;

    @JsonIgnore
    @Transient
    private MultipartFile opManualDoc;

    @Field("maintenanceGuidelinesDocName")
    private String maintenanceGuidelinesDocName;

    @JsonIgnore
    @Transient
    private MultipartFile maintenanceGuidelinesDoc;

    @Field("incidentHistory")
    private String incidentHistory;

    public UINApplication() {
        setCreatedDate(new Date());
    }

    public String getImportPermissionDocName() { return this.importPermissionDocName; }

    public void setImportPermissionDocName(String importPermissionDocName) { this.importPermissionDocName = importPermissionDocName; }

    public MultipartFile getImportPermissionDoc() { return this.importPermissionDoc; }

    public void setImportPermissionDoc(MultipartFile importPermissionDoc) { this.importPermissionDoc = importPermissionDoc; }

    public String getCinDocName() { return this.cinDocName; }

    public void setCinDocName(String cinDocName) { this.cinDocName = cinDocName; }

    public MultipartFile getCinDoc() { return this.cinDoc; }

    public void setCinDoc(MultipartFile cinDoc) { this.cinDoc = cinDoc; }

    public String getGstinDocName() { return this.gstinDocName; }

    public void setGstinDocName(String gstinDocName) { this.gstinDocName = gstinDocName; }

    public MultipartFile getGstinDoc() { return this.gstinDoc; }

    public void setGstinDoc(MultipartFile gstinDoc) { this.gstinDoc = gstinDoc; }

    public String getPanCardDocName() { return panCardDocName; }

    public void setPanCardDocName(String panCardDocName) { this.panCardDocName = panCardDocName; }

    public MultipartFile getPanCardDoc() { return panCardDoc; }

    public void setPanCardDoc(MultipartFile panCardDoc) { this.panCardDoc = panCardDoc; }

    public String getSecurityClearanceDocName() { return securityClearanceDocName; }

    public void setSecurityClearanceDocName(String securityClearanceDocName) { this.securityClearanceDocName = securityClearanceDocName; }

    public MultipartFile getSecurityClearanceDoc() {  return securityClearanceDoc; }

    public void setSecurityClearanceDoc(MultipartFile securityClearanceDoc) { this.securityClearanceDoc = securityClearanceDoc; }

    public String getDotPermissionDocName() { return dotPermissionDocName; }

    public void setDotPermissionDocName(String dotPermissionDocName) { this.dotPermissionDocName = dotPermissionDocName; }

    public MultipartFile getDotPermissionDoc() { return dotPermissionDoc; }

    public void setDotPermissionDoc(MultipartFile dotPermissionDoc) { this.dotPermissionDoc = dotPermissionDoc; }

    public String getEtaDocName() { return etaDocName; }

    public void setEtaDocName(String etaDocName) { this.etaDocName = etaDocName; }

    public MultipartFile getEtaDoc() { return etaDoc; }

    public void setEtaDoc(MultipartFile etaDoc) { this.etaDoc = etaDoc; }

    public String getFeeDetails() { return feeDetails; }

    public long getDroneTypeId() { return droneTypeId; }

    public void setDroneTypeId(long droneTypeId) { this.droneTypeId = droneTypeId; }

    public String getModelName() { return modelName; }

    public void setModelName(String modelName) { this.modelName = modelName; }

    public long getOperatorId() { return operatorId; }

    public void setOperatorId(long operatorId) { this.operatorId = operatorId; }

    public long getOperatorDroneId() { return operatorDroneId; }

    public void setOperatorDroneTypeId(long operatorDroneId) { this.operatorDroneId = operatorDroneId; }

    public void setFeeDetails(String feeDetails) { this.feeDetails = feeDetails; }

    public String getManufacturer() { return manufacturer; }

    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public long getManufacturerId() { return manufacturerId; }

    public void setManufacturerId(long manufacturerId) { this.manufacturerId = manufacturerId; }

    public AddressDocument getManufacturerAddress() { return manufacturerAddress; }

    public void setManufacturerAddress(AddressDocument manufacturerAddress) { this.manufacturerAddress = manufacturerAddress; }

    public String getManufacturerNationality() { return manufacturerNationality; }

    public void setManufacturerNationality(String manufacturerNationality) { this.manufacturerNationality = manufacturerNationality; }

    public String getModelNo() { return modelNo; }

    public void setModelNo(String modelNo) { this.modelNo = modelNo; }

    public String getSerialNo() { return serialNo; }

    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }

    public LocalDate getDateOfManufacture() { return dateOfManufacture; }

    public void setDateOfManufacture(LocalDate dateOfManufacture) { this.dateOfManufacture = dateOfManufacture; }

    public String getWingType() { return wingType; }

    public void setWingType(String wingType) { this.wingType = wingType; }

    public Boolean getIsNew() { return isNew; }

    public void setIsNew(Boolean isNew) { this.isNew = isNew; }

    public float getMaxTakeOffWeight() { return maxTakeOffWeight; }

    public void setMaxTakeOffWeight(float maxTakeOffWeight) { this.maxTakeOffWeight = maxTakeOffWeight; }

    public float getMaxHeightAttainable() { return maxHeightAttainable; }

    public void setMaxHeightAttainable(float maxHeightAttainable) { this.maxHeightAttainable = maxHeightAttainable; }

    public String getCompatiblePayload() { return compatiblePayload; }

    public void setCompatiblePayload(String compatiblePayload) { this.compatiblePayload = compatiblePayload; }

    public DroneCategoryType getDroneCategoryType() { return droneCategoryType; }

    public void setDroneCategoryType(DroneCategoryType droneCategoryType) { this.droneCategoryType = droneCategoryType; }

    public String getRegionOfOperation() { return regionOfOperation; }

    public void setRegionOfOperation(String regionOfOperation) { this.regionOfOperation = regionOfOperation; }

    public String getPurposeOfOperation() { return purposeOfOperation; }

    public void setPurposeOfOperation(String purposeOfOperation) { this.purposeOfOperation = purposeOfOperation; }

    public String getEngineType() { return engineType; }

    public void setEngineType(String engineType) { this.engineType = engineType; }

    public float getEnginePower() { return enginePower; }

    public void setEnginePower(float enginePower) { this.enginePower = enginePower; }

    public int getEngineCount() { return engineCount; }

    public void setEngineCount(int engineCount) { this.engineCount = engineCount; }

    public float getFuelCapacity() { return fuelCapacity; }

    public void setFuelCapacity(float fuelCapacity) { this.fuelCapacity = fuelCapacity; }

    public String getPropellerDetails() { return propellerDetails; }

    public void setPropellerDetails(String propellerDetails) { this.propellerDetails = propellerDetails; }

    public DroneDimensions getDimensions() { return dimensions; }

    public void setDimensions(DroneDimensions dimensions) { this.dimensions = dimensions; }

    public int getMaxEndurance() { return maxEndurance; }

    public void setMaxEndurance(int maxEndurance) { this.maxEndurance = maxEndurance; }

    public float getMaxRange() { return maxRange; }

    public void setMaxRange(float maxRange) { this.maxRange = maxRange; }

    public float getMaxSpeed() { return maxSpeed; }

    public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }

    public float getMaxHeightOfOperation() { return maxHeightOfOperation; }

    public void setMaxHeightOfOperation(float maxHeightOfOperation) { this.maxHeightOfOperation = maxHeightOfOperation; }

    public boolean getHasGNSS() { return hasGNSS; }

    public void setHasGNSS(boolean hasGNSS) { this.hasGNSS = hasGNSS; }

    public boolean HasAutonomousFlightTerminationSystem() { return hasAutonomousFlightTerminationSystem; }

    public void setHasAutonomousFlightTerminationSystem(boolean hasAutonomousFlightTerminationSystem) { this.hasAutonomousFlightTerminationSystem = hasAutonomousFlightTerminationSystem; }

    public boolean isHasFlashingAntiCollisionStrobeLights() { return hasFlashingAntiCollisionStrobeLights; }

    public void setHasFlashingAntiCollisionStrobeLights(boolean hasFlashingAntiCollisionStrobeLights) { this.hasFlashingAntiCollisionStrobeLights = hasFlashingAntiCollisionStrobeLights; }

    public boolean isHasRFID_GSM_SIMCard() { return hasRFID_GSM_SIMCard; }

    public void setHasRFID_GSM_SIMCard(boolean hasRFID_GSM_SIMCard) { this.hasRFID_GSM_SIMCard = hasRFID_GSM_SIMCard; }

    public boolean isHasFlightController() { return hasFlightController; }

    public void setHasFlightController(boolean hasFlightController) { this.hasFlightController = hasFlightController; }

    public String getPreviousUIN() { return previousUIN; }

    public void setPreviousUIN(String previousUIN) { this.previousUIN = previousUIN; }

    public String getOpManualDocName() { return opManualDocName; }

    public void setOpManualDocName(String opManualDocName) { this.opManualDocName = opManualDocName; }

    public MultipartFile getOpManualDoc() { return opManualDoc; }

    public void setOpManualDoc(MultipartFile opManualDoc) { this.opManualDoc = opManualDoc; }

    public String getMaintenanceGuidelinesDocName() { return maintenanceGuidelinesDocName; }

    public void setMaintenanceGuidelinesDocName(String maintenanceGuidelinesDocName) { this.maintenanceGuidelinesDocName = maintenanceGuidelinesDocName; }

    public MultipartFile getMaintenanceGuidelinesDoc() { return maintenanceGuidelinesDoc; }

    public void setMaintenanceGuidelinesDoc(MultipartFile maintenanceGuidelinesDoc) { this.maintenanceGuidelinesDoc = maintenanceGuidelinesDoc; }

    public String getIncidentHistory() { return incidentHistory; }

    public void setIncidentHistory(String incidentHistory) { this.incidentHistory = incidentHistory; }

    public String getUniqueDeviceId() { return uniqueDeviceId; }

    public void setUniqueDeviceId(String uniqueDeviceId) { this.uniqueDeviceId = uniqueDeviceId; }

    public List<MultipartFile> getAllDocs() {

        ArrayList<MultipartFile> list = new ArrayList<>();
        if (importPermissionDoc != null && !importPermissionDoc.isEmpty()) {
            list.add(importPermissionDoc);
        }
        if (cinDoc != null && !cinDoc.isEmpty()) {
            list.add(cinDoc);
        }
        if (gstinDoc != null && !gstinDoc.isEmpty()) {
            list.add(gstinDoc);
        }
        if (panCardDoc != null && !panCardDoc.isEmpty()) {
            list.add(panCardDoc);
        }
        if (securityClearanceDoc != null && !securityClearanceDoc.isEmpty()) {
            list.add(securityClearanceDoc);
        }
        if (dotPermissionDoc != null && !dotPermissionDoc.isEmpty()) {
            list.add(dotPermissionDoc);
        }
        if (etaDoc != null && !etaDoc.isEmpty()) {
            list.add(etaDoc);
        }
        if (opManualDoc!= null && !opManualDoc.isEmpty()) {
            list.add(opManualDoc);
        }
        if (maintenanceGuidelinesDoc != null && !maintenanceGuidelinesDoc.isEmpty()) {
            list.add(maintenanceGuidelinesDoc);
        }

        return list;
    }

}
