package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.TokenRequest;
import com.ispirit.digitalsky.dto.TokenResponse;
import com.ispirit.digitalsky.service.api.SecurityTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityTokenService securityTokenService;


    @PostMapping("/token")
    public ResponseEntity<TokenResponse> authenticateUser(@Valid @RequestBody TokenRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = securityTokenService.generateToken(authentication);
        return ResponseEntity.ok(new TokenResponse(accessToken, userPrincipal.getId(), userPrincipal.getUsername()));
    }
}
