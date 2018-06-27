package com.ispirit.digitalsky.service.api;

import org.springframework.security.core.Authentication;

public interface SecurityTokenService {

    String generateToken(Authentication authentication);

    String getUserIdFromJWT(String token);

    boolean validateToken(String token);
}
