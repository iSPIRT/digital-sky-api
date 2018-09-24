package com.ispirit.digitalsky.domain;

public class UserProfile {

    private long id;

    private long pilotProfileId;

    private long individualOperatorId;

    private long orgOperatorId;

    private long manufacturerId;

    public UserProfile(long id, long pilotProfileId, long individualOperatorId, long orgOperatorId, long manufacturerId) {
        this.id = id;
        this.pilotProfileId = pilotProfileId;
        this.individualOperatorId = individualOperatorId;
        this.orgOperatorId = orgOperatorId;
        this.manufacturerId = manufacturerId;
    }

    public boolean owns(OperatorDrone operatorDrone) {
        if (operatorDrone.getOperatorType().equals(ApplicantType.INDIVIDUAL)) {
            return individualOperatorId == operatorDrone.getOperatorId();
        }
        return orgOperatorId == operatorDrone.getOperatorId();
    }

    public long getId() {
        return id;
    }

    public long getPilotProfileId() {
        return pilotProfileId;
    }

    public long getIndividualOperatorId() {
        return individualOperatorId;
    }

    public long getOrgOperatorId() {
        return orgOperatorId;
    }

    public long getManufacturerId() {
        return manufacturerId;
    }

    public boolean isOperator() {
        return individualOperatorId != 0 || orgOperatorId != 0;
    }

    public boolean isIndividualOperator() {
        return isOperator() && individualOperatorId != 0;
    }

    public boolean isOrganizationOperator() {
        return isOperator() && orgOperatorId != 0;
    }
}
