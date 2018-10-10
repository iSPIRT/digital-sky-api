package com.ispirit.digitalsky.exception;

public class InvalidDigitalSignatureException extends RuntimeException {

    public InvalidDigitalSignatureException() {
        super("Invalid/Missing digital signature");
    }
}
