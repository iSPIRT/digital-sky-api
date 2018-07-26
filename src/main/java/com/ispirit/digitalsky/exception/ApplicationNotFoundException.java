package com.ispirit.digitalsky.exception;

public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException() {
        super("Application Form Not Found");
    }
}