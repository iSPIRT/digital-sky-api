package com.ispirit.digitalsky.exception;

public class ManufacturerExistsException extends RuntimeException {

    public ManufacturerExistsException() {
        super("Manufacturer Profile Already Exists");
    }
}
