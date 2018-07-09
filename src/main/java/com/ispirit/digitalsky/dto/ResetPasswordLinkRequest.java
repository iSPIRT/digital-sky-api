package com.ispirit.digitalsky.dto;

public class ResetPasswordLinkRequest {

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
