package com.ispirit.digitalsky.domain;

public class UserProfile {

    private long id;

    private long pilotProfileId;

    private long individualOperatorId;

    private long orgOperatorId;

    public UserProfile(long id, long pilotProfileId, long individualOperatorId, long orgOperatorId) {
        this.id = id;
        this.pilotProfileId = pilotProfileId;
        this.individualOperatorId = individualOperatorId;
        this.orgOperatorId = orgOperatorId;
    }

    public boolean owns(OperatorDrone operatorDrone) {
        if (operatorDrone.getOperatorType().equals(ApplicantType.INDIVIDUAL)) {
            return individualOperatorId == operatorDrone.getOperatorId();
        }
        return orgOperatorId == operatorDrone.getOperatorId();
    }
}
