package com.ispirit.digitalsky.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entity, String email) {
        super(String.format("Could not find %s with email %s", entity, email));
    }

    public EntityNotFoundException(String entity, long id) {
        super(String.format("Could not find %s with id %s", entity, id));
    }
}
