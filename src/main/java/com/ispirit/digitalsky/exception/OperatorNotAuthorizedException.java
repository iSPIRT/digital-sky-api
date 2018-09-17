package com.ispirit.digitalsky.exception;

public class OperatorNotAuthorizedException extends RuntimeException {

    public OperatorNotAuthorizedException() {
        super("Device not assigned to the operator");
    }
}
