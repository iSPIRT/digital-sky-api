package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.ResetPasswordEmail;
import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.repository.UserRepository;
import com.ispirit.digitalsky.service.api.EmailService;
import com.ispirit.digitalsky.service.api.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static java.lang.String.format;

public class CustomUserDetailService implements UserService {


    private UserRepository userRepository;
    private EmailService emailService;
    private String resetPasswordBasePath;

    public CustomUserDetailService(UserRepository userRepository, EmailService emailService, String resetPasswordBasePath) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.resetPasswordBasePath = resetPasswordBasePath;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.loadByEmail(email);
        return new UserPrincipal(user);
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findOne(id);
    }


    @Override
    public void generateResetPasswordLink(String email) {
        User user = userRepository.loadByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException(User.class.getSimpleName(), email);
        }
        String resetPasswordToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetPasswordToken);
        userRepository.save(user);
        String resetPasswordLink = format("%s?token=%s", resetPasswordBasePath, resetPasswordToken);
        emailService.send(new ResetPasswordEmail(user.getEmail(), resetPasswordLink));
    }

    @Override
    public void resetPassword(String token, String newPasswordHash) {
        User user = userRepository.loadByResetPasswordToken(token);
        if (user == null) {
            throw new EntityNotFoundException("ResetPasswordToken", token);
        }
        user.setPassword(newPasswordHash);
        userRepository.save(user);
    }

}
