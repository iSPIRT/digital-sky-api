package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.dto.TokenRequest;
import com.ispirit.digitalsky.dto.TokenResponse;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.repository.PilotRepository;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import com.ispirit.digitalsky.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ispirit.digitalsky.util.AuthenticationUtil.generateTokenResponse;
import static com.ispirit.digitalsky.util.AuthenticationUtil.setSecurityUserSecurityContext;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;

    private SecurityTokenService securityTokenService;

    private UserProfileService userProfileService;


    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, SecurityTokenService securityTokenService, UserProfileService userProfileService) {
        this.authenticationManager = authenticationManager;
        this.securityTokenService = securityTokenService;
        this.userProfileService = userProfileService;
    }

    @PostMapping("/token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody TokenRequest loginRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new Errors("Invalid email or password"), HttpStatus.UNAUTHORIZED);
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (!userPrincipal.isAccountVerified()) {
            return new ResponseEntity<>(new Errors("Account not verified, please check your inbox for verification link"), HttpStatus.UNAUTHORIZED);
        }

        String accessToken = setSecurityUserSecurityContext(securityTokenService, authentication);

        return generateTokenResponse(userPrincipal, userProfileService.profile(userPrincipal.getId()), accessToken);
    }
}
