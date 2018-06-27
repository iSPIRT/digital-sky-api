package com.ispirit.digitalsky.dto;

public class TokenRequest {

    private String email;

    private String password;

    private TokenRequest() {
        //for serialization and de-serialization
    }

    public TokenRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
