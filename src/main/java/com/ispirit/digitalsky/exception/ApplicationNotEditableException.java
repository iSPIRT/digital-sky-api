package com.ispirit.digitalsky.exception;

public class ApplicationNotEditableException extends RuntimeException {

    public ApplicationNotEditableException() {
        super("Application not in draft status, cannot be modified");
    }
}