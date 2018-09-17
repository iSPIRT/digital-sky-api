package com.ispirit.digitalsky.domain;

public class RegisterDroneRequestPayload {

    private DroneDevice drone;

    private String signature;

    private String digitalCertificate;

    public String getSignature() { return signature; }

    public void setSignature(String signature) { this.signature = signature; }

    public String getDigitalCertificate() { return digitalCertificate; }

    public void setDigitalCertificate(String digitalCertificate) { this.digitalCertificate = digitalCertificate; }

    public DroneDevice getDrone() { return drone; }

    public void setDrone(DroneDevice drone) { this.drone = drone; }
}
