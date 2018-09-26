package com.ispirit.digitalsky.exception;

public class ManufacturerNotFoundException extends RuntimeException {

    public ManufacturerNotFoundException() {
        super("Manufacturer not found");
    }
}
