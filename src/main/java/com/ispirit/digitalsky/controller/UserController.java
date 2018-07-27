package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.EntityId;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.dto.ResetPasswordLinkRequest;
import com.ispirit.digitalsky.dto.ResetPasswordRequest;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.ispirit.digitalsky.controller.UserController.USER_RESOURCE_BASE_PATH;
import static org.springframework.util.StringUtils.containsWhitespace;
import static org.springframework.util.StringUtils.isEmpty;

@RestController
@RequestMapping(USER_RESOURCE_BASE_PATH)
public class UserController {

    public static final String USER_RESOURCE_BASE_PATH = "/api/user";

    private UserService userService;

    PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@RequestBody User userPayload) {

        if (!validate(userPayload)) {
            return new ResponseEntity<>(new Errors("Invalid Payload"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(userPayload.getFullName(), userPayload.getEmail(), passwordEncoder.encode(userPayload.getPassword()));

        User existingUser = userService.loadByEmail(user.getEmail());
        if (existingUser != null) {
            return new ResponseEntity<>(new Errors("Email id already exist"), HttpStatus.CONFLICT);
        }
        User newUser = userService.createNew(user);
        return new ResponseEntity<>(new EntityId(newUser.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (!userPrincipal.isAdmin() && userPrincipal.getId() != id) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        User user = userService.find(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Errors("User not found"), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/resetPasswordLink", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPasswordLink(@RequestBody ResetPasswordLinkRequest resetPasswordLinkRequest, HttpServletRequest request) {

        try {
            userService.generateResetPasswordLink(resetPasswordLinkRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        try {
            userService.resetPassword(resetPasswordRequest.getToken(), passwordEncoder.encode(resetPasswordRequest.getPassword()));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    private boolean validate(User userPayload) {
        if (isEmpty(userPayload.getFullName())) return false;
        if (isEmpty(userPayload.getEmail()) || containsWhitespace(userPayload.getEmail())) return false;
        if (isEmpty(userPayload.getPassword())
                || containsWhitespace(userPayload.getPassword())
                || userPayload.getEmail().length() < 4) return false;
        return true;
    }

}
