package com.ispirit.digitalsky.exception;

public class ReCaptchaVerificationFailedException extends RuntimeException {

    public ReCaptchaVerificationFailedException() {
        super("ReCaptcha Verification Failed Exception");
    }
}