package com.ispirit.digitalsky.domain;

public class DronePayload {

    private Drone drone;

    private String signature;

    private String digitalCertificate;

    public String getSignature() { return signature; }

    public void setSignature(String signature) { this.signature = signature; }

    public String getDigitalCertificate() { return digitalCertificate; }

    public void setDigitalCertificate(String digitalCertificate) { this.digitalCertificate = digitalCertificate; }

    public Drone getDrone() { return drone; }

    public void setDrone(Drone drone) { this.drone = drone; }
}
