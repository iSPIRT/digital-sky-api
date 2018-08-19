package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.document.BasicApplication;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.dto.*;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.exception.ReCaptchaVerificationFailedException;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.service.api.ReCaptchaService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import com.ispirit.digitalsky.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.ispirit.digitalsky.controller.UserController.USER_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(USER_RESOURCE_BASE_PATH)
public class UserController {

    public static final String USER_RESOURCE_BASE_PATH = "/api/user";

    private UserService userService;
    private IndividualOperatorRepository individualOperatorRepository;

    PasswordEncoder passwordEncoder;
    private ReCaptchaService reCaptchaService;
    private UserProfileService userProfileService;

    @Autowired
    public UserController(UserService userService, IndividualOperatorRepository individualOperatorRepository, PasswordEncoder passwordEncoder, ReCaptchaService reCaptchaService, UserProfileService userProfileService) {
        this.userService = userService;
        this.individualOperatorRepository = individualOperatorRepository;
        this.passwordEncoder = passwordEncoder;
        this.reCaptchaService = reCaptchaService;
        this.userProfileService = userProfileService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@Valid @RequestBody User userPayload) {

        try {
            reCaptchaService.verifyCaptcha(userPayload.getReCaptcha());
        } catch (ReCaptchaVerificationFailedException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        User user = new User(userPayload.getFullName(), userPayload.getEmail(), passwordEncoder.encode(userPayload.getPassword()));

        User existingUser = userService.loadByEmail(user.getEmail());
        if (existingUser != null) {
            return new ResponseEntity<>(new Errors("Email id already exist"), HttpStatus.CONFLICT);
        }
        User newUser =  userService.createNew(user);

        userService.sendEmailVerificationLink(user);

        return new ResponseEntity<>(new EntityId(newUser.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (!userPrincipal.isAdmin() && userPrincipal.getId() != id) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        User user = userService.find(id);
        user.setPassword("");
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Errors("User not found"), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/applications", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> applications() {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        List<BasicApplication> applications = userService.applications(userPrincipal.getId());
        return new ResponseEntity<>(applications.stream().map(ApplicationAbstract::new).collect(Collectors.toList()), HttpStatus.OK);
    }

    @RequestMapping(value = "/drones", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> drones() {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();

        boolean isIndividual = individualOperatorRepository.loadByResourceOwner(userPrincipal.getId()) != null;
        ApplicantType operatorType = isIndividual ? ApplicantType.INDIVIDUAL : ApplicantType.ORGANISATION;

        List<?> userDrones = userService.drones(userPrincipal.getId(),operatorType );
        return new ResponseEntity<>(userDrones, HttpStatus.OK);
    }

    @RequestMapping(value = "/drones/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> drone(@PathVariable("id") long id) {

        OperatorDrone userDrone = userService.drone(id);
        if (userDrone == null) {
            return new ResponseEntity<>(new Errors("Drone not found"), HttpStatus.NOT_FOUND);
        }

        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());
        if (!profile.owns(userDrone)) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(userDrone, HttpStatus.OK);
    }


    @RequestMapping(value = "/resetPasswordLink", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPasswordLink(@Valid @RequestBody ResetPasswordLinkRequest resetPasswordLinkRequest, HttpServletRequest request) {

        try {
            userService.generateResetPasswordLink(resetPasswordLinkRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        try {
            userService.resetPassword(resetPasswordRequest.getToken(), passwordEncoder.encode(resetPasswordRequest.getPassword()));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verify(@Valid @RequestBody AccountVerificationRequest accountVerificationRequest) {

        try {
            userService.verifyAccount(accountVerificationRequest.getToken());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}
