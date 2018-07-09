package com.ispirit.digitalsky.dto;

public class ResetPasswordRequest {

    private String token;

    private String password;

    private ResetPasswordRequest() {
        //for serialization and de-serialization
    }

    public ResetPasswordRequest(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }
}
