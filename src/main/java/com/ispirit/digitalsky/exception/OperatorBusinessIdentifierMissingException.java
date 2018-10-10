package com.ispirit.digitalsky.exception;

public class OperatorBusinessIdentifierMissingException extends RuntimeException {

    public OperatorBusinessIdentifierMissingException() {
        super("Operator business identifier missing in the payload");
    }
}

