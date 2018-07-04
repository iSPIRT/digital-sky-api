package com.ispirit.digitalsky.dto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Errors {

    private List<String> errors = new ArrayList<>();

    private Errors() {
        //for serialization and de-serialization
    }

    public Errors(String... errors) {
        if (errors != null && errors.length > 0) {
            this.errors.addAll(asList(errors));
        }
    }

    public List<String> getErrors() {
        return errors;
    }
}
