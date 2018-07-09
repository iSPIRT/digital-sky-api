package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.dto.ResetPasswordLinkRequest;
import com.ispirit.digitalsky.dto.ResetPasswordRequest;
import com.ispirit.digitalsky.dto.UserId;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.repository.UserRepository;
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

    public static final String RESET_PASSWORD_PATH = USER_RESOURCE_BASE_PATH + "/resetPassword";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@RequestBody User userPayload) {

        if (!validate(userPayload)) {
            return new ResponseEntity<>(new Errors("Invalid Payload"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(userPayload.getFullName(), userPayload.getEmail(), passwordEncoder.encode(userPayload.getPassword()));

        User existingUser = userRepository.loadByEmail(user.getEmail());
        if (existingUser != null) {
            return new ResponseEntity<>(new Errors("Email id already exist"), HttpStatus.CONFLICT);
        }
        User newUser = userRepository.save(user);
        return new ResponseEntity<>(new UserId(newUser), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        User user = userRepository.findOne(id);
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
