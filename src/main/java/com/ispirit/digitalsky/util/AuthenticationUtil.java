package com.ispirit.digitalsky.util;

import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.dto.TokenResponse;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtil {

    public static String setSecurityUserSecurityContext(SecurityTokenService securityTokenService, UserPrincipal userPrincipal) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return securityTokenService.generateToken(authentication);
    }

    public static String setSecurityUserSecurityContext(SecurityTokenService securityTokenService, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return securityTokenService.generateToken(authentication);
    }

    public static ResponseEntity<?> generateTokenResponse(UserPrincipal userPrincipal, UserProfile userProfile, String accessToken) {
        if (userPrincipal.isAdmin()) {
            return ResponseEntity.ok(
                    TokenResponse.adminUserResponse(accessToken, userPrincipal.getId(), userPrincipal.getUsername())
            );
        }

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userProfile.getPilotProfileId(),
                userProfile.getIndividualOperatorId(),
                userProfile.getOrgOperatorId())
        );
    }
}
