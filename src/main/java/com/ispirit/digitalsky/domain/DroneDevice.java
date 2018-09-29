package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.util.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "ds_drone_device")
public class DroneDevice implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @Column(name = "DRONE_TYPE_ID")
    private long droneTypeId;

    @JsonIgnore
    @Column(name = "CREATED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate createdDate;

    @JsonIgnore
    @Column(name = "LAST_MODIFIED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate lastModifiedDate;

    @JsonIgnore
    @Column(name = "MANUFACTURER_BUSINESS_IDENTIFIER", nullable = false)
    private String manufacturerBusinessIdentifier;

    @JsonIgnore
    @Column(name = "REGISTRATION_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private DroneDeviceRegistrationStatus registrationStatus  = DroneDeviceRegistrationStatus.NOT_REGISTERED;

    @Column(name = "VERSION", nullable = false)
    private String version;

    @Column(name = "TXN", nullable = false)
    private String txn;

    @Column(name = "UNIQUE_DEVICE_ID", nullable = false)
    private String deviceId;

    @Column(name = "DEVICE_MODEL_ID", nullable = false)
    private String deviceModelId;

    @Column(name = "OPERATOR_BUSINESS_IDENTIFIER")
    private String operatorBusinessIdentifier;

    @Column(name = "ID_HASH")
    private String idHash;

    public DroneDevice() {

    }

    public long getDroneTypeId() {
        return droneTypeId;
    }

    public void setDroneTypeId(long droneTypeId) {
        this.droneTypeId = droneTypeId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTxn() {
        return txn;
    }

    public void setTxn(String txn) {
        this.txn = txn;
    }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(String deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public String getOperatorBusinessIdentifier() { return operatorBusinessIdentifier; }

    public void setOperatorBusinessIdentifier(String operatorBusinessIdentifier) {
        this.operatorBusinessIdentifier = operatorBusinessIdentifier;
    }

    public String getManufacturerBusinessIdentifier() { return manufacturerBusinessIdentifier; }

    public void setManufacturerBusinessIdentifier(String manufacturerBusinessIdentifier) { this.manufacturerBusinessIdentifier = manufacturerBusinessIdentifier; }

    public String getIdHash() {
        return idHash;
    }

    public void setIdHash(String idHash) { this.idHash = idHash; }

    public DroneDeviceRegistrationStatus getRegistrationStatus() { return registrationStatus; }

    public void setRegistrationStatus(DroneDeviceRegistrationStatus registrationStatus) { this.registrationStatus = registrationStatus; }
}
