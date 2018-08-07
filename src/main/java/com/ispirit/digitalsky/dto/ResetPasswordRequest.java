package com.ispirit.digitalsky.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotNull
    private String token;

    @NotNull
    @Size(min = 8, max = 20)
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
