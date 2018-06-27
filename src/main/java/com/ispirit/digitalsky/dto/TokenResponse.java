package com.ispirit.digitalsky.dto;

public class TokenResponse {

    private String accessToken;

    private String tokenType = "Bearer";

    private TokenResponse() {
        //for serialization and de-serialization
    }

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

}
