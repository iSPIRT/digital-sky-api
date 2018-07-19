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

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;

    private SecurityTokenService securityTokenService;

    private PilotRepository pilotRepository;

    private IndividualOperatorRepository individualOperatorRepository;

    private OrganizationOperatorRepository organizationOperatorRepository;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, SecurityTokenService securityTokenService, PilotRepository pilotRepository, IndividualOperatorRepository individualOperatorRepository, OrganizationOperatorRepository organizationOperatorRepository) {
        this.authenticationManager = authenticationManager;
        this.securityTokenService = securityTokenService;
        this.pilotRepository = pilotRepository;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
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
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = securityTokenService.generateToken(authentication);

        Pilot pilot = pilotRepository.loadByResourceOwner(userPrincipal.getId());
        IndividualOperator individualOperator = individualOperatorRepository.loadByResourceOwner(userPrincipal.getId());
        OrganizationOperator organizationOperator = organizationOperatorRepository.loadByResourceOwner(userPrincipal.getId());

        long pilotProfileId = (pilot != null) ? pilot.getId() : 0;
        long individualOperatorProfileId = (individualOperator != null) ? individualOperator.getId() : 0;
        long organizationOperatorProfileId = (organizationOperator != null) ? organizationOperator.getId() : 0;

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                pilotProfileId,
                individualOperatorProfileId,
                organizationOperatorProfileId)
        );
    }
}
