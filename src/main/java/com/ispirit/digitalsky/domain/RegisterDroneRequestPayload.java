package com.ispirit.digitalsky.domain;


import javax.validation.constraints.NotNull;

public class RegisterDroneRequestPayload {

    @NotNull
    private DroneDevice drone;

    @NotNull
    private String signature;

    @NotNull
    private String digitalCertificate;

    public RegisterDroneRequestPayload() { }

    public RegisterDroneRequestPayload(DroneDevice drone, String signature, String digitalCertificate ) {
        this.drone = drone;
        this.signature = signature;
        this.digitalCertificate = digitalCertificate;
    }

    public String getSignature() { return signature; }

    public void setSignature(String signature) { this.signature = signature; }

    public String getDigitalCertificate() { return digitalCertificate; }

    public void setDigitalCertificate(String digitalCertificate) { this.digitalCertificate = digitalCertificate; }

    public DroneDevice getDrone() { return drone; }

    public void setDrone(DroneDevice drone) { this.drone = drone; }
}
