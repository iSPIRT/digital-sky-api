package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.dto.UserId;
import com.ispirit.digitalsky.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.StringUtils.containsWhitespace;
import static org.springframework.util.StringUtils.isEmpty;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

    private boolean validate(User userPayload) {
        if (isEmpty(userPayload.getFullName())) return false;
        if (isEmpty(userPayload.getEmail()) || containsWhitespace(userPayload.getEmail())) return false;
        if (isEmpty(userPayload.getPassword())
                || containsWhitespace(userPayload.getPassword())
                || userPayload.getEmail().length() < 4) return false;
        return true;
    }

}
