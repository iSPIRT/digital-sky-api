package com.ispirit.digitalsky.exception;

public class ApplicationFormNotFoundException extends RuntimeException {

    public ApplicationFormNotFoundException() {
        super("Application Form Not Found");
    }
}