package com.ispirit.digitalsky.exception;

public class ApplicationNotInSubmittedStatus extends RuntimeException {

    public ApplicationNotInSubmittedStatus() {
        super("Application Form Can Only be Approved or Rejected When in DRAFT Status");
    }
}