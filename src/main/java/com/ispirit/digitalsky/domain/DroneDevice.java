package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.util.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_drone_device")
public class DroneDevice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "DRONE_TYPE_ID")
    private long droneTypeId;

    @Column(name = "CREATED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate createdDate;

    @Column(name = "LAST_MODIFIED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate lastModifiedDate;

    @Column(name = "MANUFACTURER_ID", nullable = false)
    @JsonIgnore
    private String manufacturerId;

    /* attributes copied from request */
    @Column(name = "VERSION", nullable = false)
    private String version;

    @Column(name = "TXN", nullable = false)
    private String txn;

    @Column(name = "UNIQUE_DEVICE_ID", nullable = false)
    private String deviceId;

    @Column(name = "DEVICE_MODEL_ID", nullable = false)
    private String deviceModelId;

    @Column(name = "OPERATOR_CODE", nullable = false)
    private String operatorCode;

    @Column(name = "REQUEST_TIMESTAMP", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime requestTimestamp;

    @Column(name = "ID_HASH", nullable = false)
    private String idHash;

    @Column(name = "REGISTRATION_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private DroneDeviceRegistrationStatus registrationStatus  = DroneDeviceRegistrationStatus.NOT_REGISTERED;

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

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getManufacturerId() { return manufacturerId; }

    public void setManufacturerId(String manufacturerId) { this.manufacturerId = manufacturerId; }

    public LocalDateTime getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(LocalDateTime requestTimestamp) { this.requestTimestamp = requestTimestamp; }

    public String getIdHash() {
        return idHash;
    }

    public void setIdHash(String idHash) {
        this.idHash = idHash;
    }

    public DroneDeviceRegistrationStatus getRegistrationStatus() { return registrationStatus; }

    public void setRegistrationStatus(DroneDeviceRegistrationStatus registrationStatus) { this.registrationStatus = registrationStatus; }

}
