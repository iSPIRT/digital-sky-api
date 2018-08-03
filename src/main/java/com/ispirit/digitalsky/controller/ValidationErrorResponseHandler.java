package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static java.lang.String.format;

@ControllerAdvice
public class ValidationErrorResponseHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Errors errors = new Errors();
        for (FieldError fieldError : fieldErrors) {
            errors.getErrors().add(format("%s %s", fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return new ResponseEntity(errors, headers, status);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<?> handleValidationException(ValidationException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getErrors(), HttpStatus.NOT_FOUND);
    }
}
