package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.util.CustomLocalDateDeSerializer;
import com.ispirit.digitalsky.util.CustomLocalDateSerializer;
import com.ispirit.digitalsky.util.LocalDateAttributeConverter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "ds_operator_drone")
public class OperatorDrone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "DRONE_TYPE_ID")
    private DroneType droneType;

    @Column(name = "OPERATOR_ID")
    private long operatorId;

    @Column(name = "OPERATOR_TYPE")
    @Enumerated(EnumType.STRING)
    private ApplicantType operatorType;

    @Column(name = "ACQUISITION_APPLICATION_ID")
    private String acquisitionApplicationId;

    @Column(name = "IS_IMPORTED")
    private boolean isImported;

    @Column(name = "UIN_APPLICATION_ID")
    private String uinApplicationId;

    @Column(name = "REGISTERED_DATE")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate registeredDate;

    @Column(name = "OPERATOR_DRONE_STATUS")
    @Enumerated(EnumType.STRING)
    private OperatorDroneStatus operatorDroneStatus = OperatorDroneStatus.UIN_NOT_APPLIED;

    @Column(name = "UNIQUE_DEVICE_ID")
    private String deviceId;

    @Column(name = "UIN_NO")
    private String uinNo;

    @Column(name ="APPROVAL_TIME_STAMP")
    private Timestamp approvalTimestamp;

    public OperatorDrone() {

    }

    public OperatorDrone(long operatorId, ApplicantType operatorType, String acquisitionApplicationId, boolean isImported) {
        this.operatorId =  operatorId;
        this.operatorType = operatorType;
        this.acquisitionApplicationId = acquisitionApplicationId;
        this.isImported = isImported;
    }

    public long getId() {
        return id;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public ApplicantType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(ApplicantType operatorType) {
        this.operatorType = operatorType;
    }

    public String getAcquisitionApplicationId() {
        return this.acquisitionApplicationId;
    }

    public void setAcquisitionApplicationId(String acquisitionApplicationId) { this.acquisitionApplicationId = acquisitionApplicationId; }

    public boolean isImported() {
        return this.isImported;
    }

    public void setImported(boolean imported) {
        this.isImported = imported;
    }

    public String getUinApplicationId() {
        return uinApplicationId;
    }

    public void setUinApplicationId(String uinApplicationId) {
        this.uinApplicationId = uinApplicationId;
    }

    public OperatorDroneStatus getOperatorDroneStatus() {
        return operatorDroneStatus;
    }

    public void setOperatorDroneStatus(OperatorDroneStatus operatorDroneStatus) { this.operatorDroneStatus = operatorDroneStatus; }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

    public DroneType getDroneType() {
        return droneType;
    }

    public void setDroneType(DroneType droneType) {
        this.droneType = droneType;
    }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getUinNo() {
        return uinNo;
    }

    public void setUinNo(String uinNo) {
        this.uinNo = uinNo;
    }

    public void setApprovalTimestamp(Timestamp approvalTimestamp) {
        this.approvalTimestamp = approvalTimestamp;
    }
}
