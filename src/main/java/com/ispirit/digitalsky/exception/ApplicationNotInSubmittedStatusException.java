package com.ispirit.digitalsky.exception;

public class ApplicationNotInSubmittedStatusException extends RuntimeException {

    public ApplicationNotInSubmittedStatusException() {
        super("Application Form Can Only be Approved or Rejected When in DRAFT Status");
    }
}