package com.ispirit.digitalsky.exception;

public class DeviceUniqueIdMissingException extends RuntimeException{
    public DeviceUniqueIdMissingException() {
        super("Device Unique ID missing.");
    }
}
