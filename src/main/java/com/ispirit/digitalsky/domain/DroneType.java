package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.util.CustomLocalDateDeSerializer;
import com.ispirit.digitalsky.util.CustomLocalDateSerializer;
import com.ispirit.digitalsky.util.LocalDateAttributeConverter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ds_drone_type")
public class DroneType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "CREATED_BY_ID")
    private long createdBy;

    @Column(name = "CREATED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate createdDate;

    @Column(name = "LAST_MODIFIED_BY_ID")
    private long lastModifiedBy;

    @Column(name = "LAST_MODIFIED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate lastModifiedDate;

    @Column(name = "MANUFACTURER")
    private String manufacturer;

    @Embedded
    @Column(name = "MANUFACTURER_ADDRESS")
    @AttributeOverrides(value = {
            @AttributeOverride(name = "type", column = @Column(name = "MANUFACTURER_ADDRESS_TYPE")),
            @AttributeOverride(name = "lineOne", column = @Column(name = "MANUFACTURER_ADDRESS_LINE_ONE")),
            @AttributeOverride(name = "lineTwo", column = @Column(name = "MANUFACTURER_ADDRESS_LINE_TWO")),
            @AttributeOverride(name = "city", column = @Column(name = "MANUFACTURER_ADDRESS_TOWN_OR_CITY")),
            @AttributeOverride(name = "state", column = @Column(name = "MANUFACTURER_ADDRESS_STATE")),
            @AttributeOverride(name = "country", column = @Column(name = "MANUFACTURER_ADDRESS_COUNTRY")),
            @AttributeOverride(name = "pinCode", column = @Column(name = "MANUFACTURER_ADDRESS_PIN_CODE"))
    })
    private AddressEmbeddable manufacturerAddress;

    @Column(name = "MANUFACTURER_NATIONALITY")
    protected String manufacturerNationality;

    @Column(name = "MODEL_NO")
    private String modelNo;

    @Column(name = "SERIAL_NO")
    private String serialNo;

    @Column(name = "DATE_OF_MANUFACTURE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate dateOfManufacture;

    @Column(name = "YEAR_OF_MANUFACTURE")
    @Transient
    private String yearOfManufacture;

    @Column(name = "WING_TYPE")
    private String wingType;

    @Column(name = "MAX_TAKE_OFF_WEIGHT")
    private float maxTakeOffWeight;

    @Column(name = "MAX_HEIGHT_ATTAINABLE")
    private float maxHeightAttainable;

    @Column(name = "DRONE_CATEGORY_TYPE")
    @Enumerated(EnumType.STRING)
    private DroneCategoryType droneCategoryType;

    @Column(name = "COMPATIBLE_PAYLOAD")
    private String compatiblePayload;

    @Column(name = "PURPOSE_OF_OPERATION")
    private String purposeOfOperation;

    @Column(name = "PROPOSED_BASE_OF_OPERATION")
    private String proposedBaseOfOperation;

    @Column(name = "ENGINE_TYPE")
    private String engineType;

    @Column(name = "ENGINE_POWER")
    private float enginePower;

    @Column(name = "ENGINE_COUNT")
    private int engineCount;

    @Column(name = "FUEL_CAPACITY")
    private float fuelCapacity;

    @Column(name = "PROPELLER_DETAILS")
    private String propellerDetails;

    @Column(name = "DIMENSIONS")
    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "length", column = @Column(name = "LENGTH_OF_DRONE")),
            @AttributeOverride(name = "breadth", column = @Column(name = "BREADTH_OF_DRONE")),
            @AttributeOverride(name = "height", column = @Column(name = "HEIGHT_OF_DRONE"))
    })
    private DroneDimensions dimensions;

    @Column(name = "MAX_ENDURANCE")
    private int maxEndurance;

    @Column(name = "MAX_RANGE")
    private float maxRange;

    @Column(name = "MAX_SPEED")
    private float maxSpeed;

    @Column(name = "HAS_GNSS")
    private boolean hasGNSS = false;

    @Column(name = "MAX_HEIGHT_OF_OPERATION")
    private float maxHeightOfOperation;

    @Column(name = "HAS_AUTONOMOUS_FLIGHT_TERMINATION_SYSTEM")
    private boolean hasAutonomousFlightTerminationSystem = false;

    @Column(name = "HAS_FLASHING_COLLISION_STROBE_LIGHTS")
    private boolean hasFlashingAntiCollisionStrobeLights = false;

    @Column(name = "HAS_RFID_GSM_SIM_CARD")
    private boolean hasRFID_GSM_SIMCard = false;

    @Column(name = "HAS_FLIGHT_CONTROLLER")
    private boolean hasFlightController = false;

    @Column(name = "OP_MANUAL_DOC_NAME")
    private String opManualDocName;

    @JsonIgnore
    @Transient
    private MultipartFile opManualDoc;

    @Column(name = "MAINTENANCE_GUIDELINES_DOC_NAME")
    private String maintenanceGuidelinesDocName;

    @JsonIgnore
    @Transient
    private MultipartFile maintenanceGuidelinesDoc;

    public DroneType() { setCreatedDate (LocalDate.now()); }

    public long getId() { return id; }

    public long getCreatedBy() { return createdBy; }

    public void setCreatedBy(long createdBy) { this.createdBy = createdBy; }

    public LocalDate getCreatedDate() { return createdDate; }

    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public long getLastModifiedBy() { return lastModifiedBy; }

    public void setLastModifiedBy(long lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public LocalDate getLastModifiedDate() { return lastModifiedDate; }

    public void setLastModifiedDate(LocalDate lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public AddressEmbeddable getManufacturerAddress() {
        return manufacturerAddress;
    }

    public void setManufacturerAddress(AddressEmbeddable manufacturerAddress) { this.manufacturerAddress = manufacturerAddress; }

    public String getManufacturerNationality() {
        return manufacturerNationality;
    }

    public void setManufacturerNationality(String manufacturerNationality) { this.manufacturerNationality = manufacturerNationality; }

    public String getModelNo() {
        return modelNo;
    }

    public String getModelName() { return modelName; }

    public void setModelName(String modelName) { this.modelName = modelName; }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public LocalDate getDateOfManufacture() {
        return dateOfManufacture;
    }

    public void setDateOfManufacture(LocalDate dateOfManufacture) {
        this.dateOfManufacture = dateOfManufacture;
    }

    public String getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(String yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getWingType() {
        return wingType;
    }

    public void setWingType(String wingType) {
        this.wingType = wingType;
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

    public DroneCategoryType getDroneCategoryType() {
        return droneCategoryType;
    }

    public void setDroneCategoryType(DroneCategoryType droneCategoryType) { this.droneCategoryType = droneCategoryType; }

    public String getCompatiblePayload() {
        return compatiblePayload;
    }

    public void setCompatiblePayload(String compatiblePayload) {
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

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public float getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(float enginePower) {
        this.enginePower = enginePower;
    }

    public int getEngineCount() {
        return engineCount;
    }

    public void setEngineCount(int engineCount) {
        this.engineCount = engineCount;
    }

    public float getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(float fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public String getPropellerDetails() {
        return propellerDetails;
    }

    public void setPropellerDetails(String propellerDetails) {
        this.propellerDetails = propellerDetails;
    }

    public DroneDimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(DroneDimensions dimensions) {
        this.dimensions = dimensions;
    }

    public int getMaxEndurance() {
        return maxEndurance;
    }

    public void setMaxEndurance(int maxEndurance) {
        this.maxEndurance = maxEndurance;
    }

    public float getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public boolean isHasGNSS() {
        return hasGNSS;
    }

    public void setHasGNSS(boolean hasGNSS) {
        this.hasGNSS = hasGNSS;
    }

    public boolean isHasAutonomousFlightTerminationSystem() {
        return hasAutonomousFlightTerminationSystem;
    }

    public void setHasAutonomousFlightTerminationSystem(boolean hasAutonomousFlightTerminationSystem) {
        this.hasAutonomousFlightTerminationSystem = hasAutonomousFlightTerminationSystem;
    }

    public boolean isHasFlashingAntiCollisionStrobeLights() {
        return hasFlashingAntiCollisionStrobeLights;
    }

    public void setHasFlashingAntiCollisionStrobeLights(boolean hasFlashingAntiCollisionStrobeLights) {
        this.hasFlashingAntiCollisionStrobeLights = hasFlashingAntiCollisionStrobeLights;
    }

    public float getMaxHeightOfOperation() {
        return maxHeightOfOperation;
    }

    public void setMaxHeightOfOperation(float maxHeightOfOperation) {
        this.maxHeightOfOperation = maxHeightOfOperation;
    }

    public boolean isHasRFID_GSM_SIMCard() {
        return hasRFID_GSM_SIMCard;
    }

    public void setHasRFID_GSM_SIMCard(boolean hasRFID_GSM_SIMCard) {
        this.hasRFID_GSM_SIMCard = hasRFID_GSM_SIMCard;
    }

    public boolean isHasFlightController() {
        return hasFlightController;
    }

    public void setHasFlightController(boolean hasFlightController) {
        this.hasFlightController = hasFlightController;
    }

    public String getOpManualDocName() {
        return opManualDocName;
    }

    public void setOpManualDocName(String opManualDocName) {
        this.opManualDocName = opManualDocName;
    }

    public MultipartFile getOpManualDoc() {
        return opManualDoc;
    }

    public void setOpManualDoc(MultipartFile opManualDoc) {
        this.opManualDoc = opManualDoc;
    }

    public String getMaintenanceGuidelinesDocName() {
        return maintenanceGuidelinesDocName;
    }

    public void setMaintenanceGuidelinesDocName(String maintenanceGuidelinesDocName) {
        this.maintenanceGuidelinesDocName = maintenanceGuidelinesDocName;
    }

    public MultipartFile getMaintenanceGuidelinesDoc() {
        return maintenanceGuidelinesDoc;
    }

    public void setMaintenanceGuidelinesDoc(MultipartFile maintenanceGuidelinesDoc) {
        this.maintenanceGuidelinesDoc = maintenanceGuidelinesDoc;
    }

    public List<MultipartFile> getAllDocs() {

        ArrayList<MultipartFile> list = new ArrayList<>();

        if (opManualDoc!= null && !opManualDoc.isEmpty()) {
            list.add(opManualDoc);
        }
        if (maintenanceGuidelinesDoc != null && !maintenanceGuidelinesDoc.isEmpty()) {
            list.add(maintenanceGuidelinesDoc);
        }

        return list;
    }

}
