package com.ispirit.digitalsky.exception;

public class DeviceNotInRegisteredStateException extends RuntimeException {

    public DeviceNotInRegisteredStateException() { super("Device not registered") ; }
}
