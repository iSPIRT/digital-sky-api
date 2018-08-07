package com.ispirit.digitalsky.dto;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

public class ResetPasswordLinkRequest {

    @NotNull
    @Email
    private String email;

    private ResetPasswordLinkRequest() {
        //for serialization and de-serialization
    }

    public ResetPasswordLinkRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
