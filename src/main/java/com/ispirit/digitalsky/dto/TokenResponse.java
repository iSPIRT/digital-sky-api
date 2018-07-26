package com.ispirit.digitalsky.dto;

public class TokenResponse {

    private String accessToken;

    private long id;

    private String username;

    private long pilotProfileId;

    private long individualOperatorProfileId;

    private long organizationOperatorProfileId;

    private String tokenType = "Bearer";

    private boolean isAdmin = false;

    private TokenResponse() {
        //for serialization and de-serialization
    }

    public TokenResponse(String accessToken, long id, String username, long pilotProfileId, long individualOperatorProfileId, long organizationOperatorProfileId) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.pilotProfileId = pilotProfileId;
        this.individualOperatorProfileId = individualOperatorProfileId;
        this.organizationOperatorProfileId = organizationOperatorProfileId;
    }

    public static TokenResponse adminUserResponse(String accessToken, long id, String username) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.id = id;
        tokenResponse.username = username;
        tokenResponse.isAdmin = true;
        return tokenResponse;
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

    public long getPilotProfileId() {
        return pilotProfileId;
    }

    public long getIndividualOperatorProfileId() {
        return individualOperatorProfileId;
    }

    public long getOrganizationOperatorProfileId() {
        return organizationOperatorProfileId;
    }
}
