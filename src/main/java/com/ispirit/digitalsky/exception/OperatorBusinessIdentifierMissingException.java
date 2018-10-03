package com.ispirit.digitalsky.exception;

public class OperatorBusinessIdentifierMissingException extends RuntimeException {

    public OperatorBusinessIdentifierMissingException() {
        super("Operator business identifier in the payload");
    }
}

