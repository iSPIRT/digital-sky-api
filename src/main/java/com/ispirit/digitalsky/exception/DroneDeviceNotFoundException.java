package com.ispirit.digitalsky.exception;

public class DroneDeviceNotFoundException extends RuntimeException {

    public DroneDeviceNotFoundException() {
        super("Drone not found");
    }
}

