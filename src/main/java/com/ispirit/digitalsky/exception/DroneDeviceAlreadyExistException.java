package com.ispirit.digitalsky.exception;

public class DroneDeviceAlreadyExistException extends RuntimeException {

    public DroneDeviceAlreadyExistException() {
        super("Drone already registered");
    }
}
