package com.ispirit.digitalsky.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entity, String email) {
        super(String.format("Could not find %s with email %s", entity, email));
    }
}
