package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "ds_operator_drone")
public class OperatorDrone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @Column(name = "DRONE_TYPE_ID")
   // @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "ID", nullable = false)
    private long droneTypeId;

    @Column(name = "OPERATOR_ID")
    private long operatorId;

    @Column(name = "OPERATOR_TYPE")
    private PersonType operatorType;

    @Column(name = "ACQUISITION_APPLICATION_ID")
    private String acquisitionApplicationId;

    @Column(name = "IS_IMPORTED")
    private boolean isImported;

    @Column(name = "UIN_APPLICATION_ID")
    private long uinApplicationId;

    @Column(name = "OPERATOR_DRONE_STATUS")
    private OperatorDroneStatus operatorDroneStatus;

    public OperatorDrone() {

    }

    public OperatorDrone(long operatorId, PersonType operatorType, long droneTypeId, String acquisitionApplicationId, boolean isImported) {

        this.operatorId =  operatorId;
        this.operatorType = operatorType;
        this.droneTypeId = droneTypeId;
        this.acquisitionApplicationId = acquisitionApplicationId;
        this.isImported = isImported;
    }

    public long getId() {
        return id;
    }

    public long getDroneTypeId() {
        return droneTypeId;
    }

    public void setDroneTypeId(long droneTypeId) {
        this.droneTypeId = droneTypeId;
    }


    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public PersonType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(PersonType operatorType) {
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

    public long getUinApplicationId() {
        return uinApplicationId;
    }

    public void setUinApplicationId(long uinApplicationId) {
        this.uinApplicationId = uinApplicationId;
    }

    public OperatorDroneStatus getOperatorDroneStatus() {
        return operatorDroneStatus;
    }

    public void setOperatorDroneStatus(OperatorDroneStatus operatorDroneStatus) {
        this.operatorDroneStatus = operatorDroneStatus;
    }

}
