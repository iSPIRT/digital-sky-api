package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;


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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "REGISTERED_DATE")
    private Date registeredDate;

    @Column(name = "OPERATOR_DRONE_STATUS")
    @Enumerated(EnumType.STRING)
    private OperatorDroneStatus operatorDroneStatus = OperatorDroneStatus.UIN_NOT_APPLIED;

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

    public void setAcquisitionApplicationId(String acquisitionApplicationId) {
        this.acquisitionApplicationId = acquisitionApplicationId;
    }

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

    public void setOperatorDroneStatus(OperatorDroneStatus operatorDroneStatus) {
        this.operatorDroneStatus = operatorDroneStatus;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public DroneType getDroneType() {
        return droneType;
    }

    public void setDroneType(DroneType droneType) {
        this.droneType = droneType;
    }
}
