package com.ispirit.digitalsky.exception;

public class DeviceAlreadyUsedInAnotherUINApplicationException extends RuntimeException {
    public DeviceAlreadyUsedInAnotherUINApplicationException() {
        super("Device Id is already used in another UIN appplication");
    }
}
