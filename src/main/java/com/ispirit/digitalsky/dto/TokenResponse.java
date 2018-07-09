package com.ispirit.digitalsky.dto;

public class TokenResponse {

    private String accessToken;
    private long id;
    private String username;

    private String tokenType = "Bearer";

    private TokenResponse() {
        //for serialization and de-serialization
    }

    public TokenResponse(String accessToken, long id, String username) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
