package com.ispirit.digitalsky.dto;

public class TokenResponse {

    private String accessToken;

    private long id;

    private String username;

    private long pilotProfileId;

    private long individualOperatorProfileId;

    private long organizationOperatorProfileId;

    private long manufacturerProfileId;

    private String tokenType = "Bearer";

    private boolean isAdmin = false;

    private boolean isAtcAdmin = false;

    private boolean isAfmluAdmin = false;

    private boolean isViewerAdmin = false;
    private boolean isATCViewerAdmin = false;
    private boolean isAFMLUViewerAdmin = false;

    private TokenResponse() {
        //for serialization and de-serialization
    }

    public TokenResponse(String accessToken, long id, String username, long pilotProfileId, long individualOperatorProfileId, long organizationOperatorProfileId, long manufacturerProfileId) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.pilotProfileId = pilotProfileId;
        this.individualOperatorProfileId = individualOperatorProfileId;
        this.organizationOperatorProfileId = organizationOperatorProfileId;
        this.manufacturerProfileId =  manufacturerProfileId;
    }

    public static TokenResponse adminUserResponse(String accessToken, long id, String username) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.id = id;
        tokenResponse.username = username;
        tokenResponse.isAdmin = true;
        return tokenResponse;
    }// todo: convert this to a switch sort of scenario

    public static TokenResponse atcAdminUserResponse(String accessToken, long id, String username){
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.id = id;
        tokenResponse.username = username;
        tokenResponse.isAtcAdmin = true;
        return tokenResponse;
    }

    public static TokenResponse afmluAdminUserResponse(String accessToken, long id, String username){
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.id = id;
        tokenResponse.username = username;
        tokenResponse.isAfmluAdmin = true;
        return tokenResponse;
    }

    public static TokenResponse viewerAdminUserResponse(String accessToken, long id, String username){
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.id = id;
        tokenResponse.username = username;
        tokenResponse.isViewerAdmin = true;
        return tokenResponse;
    }
    public static TokenResponse afmluViewerAdminUserResponse(String accessToken, long id, String username){
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.id = id;
        tokenResponse.username = username;
        tokenResponse.isAFMLUViewerAdmin = true;
        return tokenResponse;
    }
    public static TokenResponse atcViewerAdminUserResponse(String accessToken, long id, String username){
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.accessToken = accessToken;
        tokenResponse.id = id;
        tokenResponse.username = username;
        tokenResponse.isATCViewerAdmin = true;
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

    public long getManufacturerProfileId() { return manufacturerProfileId; }

    public void setManufacturerProfileId(long manufacturerProfileId) { this.manufacturerProfileId = manufacturerProfileId; }

    public boolean isAdmin() {
        return isAdmin;
    }
}
