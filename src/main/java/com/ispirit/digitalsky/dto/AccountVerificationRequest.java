package com.ispirit.digitalsky.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountVerificationRequest {

    @NotNull
    private String token;

    private AccountVerificationRequest() {
        //for serialization and de-serialization
    }

    public AccountVerificationRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
