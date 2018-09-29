package com.ispirit.digitalsky.exception;

public class OperatorCodeMissingException extends RuntimeException {

    public OperatorCodeMissingException() {
        super("Operator Code missing in the payload");
    }
}

