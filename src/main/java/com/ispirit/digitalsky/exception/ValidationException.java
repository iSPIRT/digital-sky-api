package com.ispirit.digitalsky.exception;

import com.ispirit.digitalsky.dto.Errors;

public class ValidationException extends RuntimeException {

    private Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
