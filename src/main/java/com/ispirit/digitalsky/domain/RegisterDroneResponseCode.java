package com.ispirit.digitalsky.domain;

public enum RegisterDroneResponseCode {

    REGISTERED(1, "Registered"),
    REGISTRATION_FAILED(10, "Registration Failed"),
    DEREGISTERED(20, "Deregistered"),
    DEREGISTRATION_FAILED(30, "Deregistration failed"),
    OPERATOR_CODE_INVALID(40, "Operator business identifier invalid"),
    OPERATOR_CODE_MISSING(41, "Operator business identifier missing"),
    INVALID_SIGNATURE(50, "Invalid Signature"),
    INVALID_DIGITAL_CERTIFICATE(60, "Invalid Digital Certificate"),
    DRONE_ALREADY_REGISTERED(70, "Drone Already Registered"),
    INVALID_MANUFACTURER(80, "Invalid manufacturer in the certificate"),
    DRONE_NOT_FOUND(90, "Drone not found"),
    BAD_REQUEST_PAYLOAD(100, "BAD request payload");

    private final int value;
    private final String reasonPhrase;

    private RegisterDroneResponseCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String toString() {
        return Integer.toString(this.value);
    }

    public static RegisterDroneResponseCode valueOf(int responseCode) {
        RegisterDroneResponseCode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            RegisterDroneResponseCode response = var1[var3];
            if (response.value == responseCode) {
                return response;
            }
        }

        throw new IllegalArgumentException("No matching constant for [" + responseCode + "]");
    }

    }
