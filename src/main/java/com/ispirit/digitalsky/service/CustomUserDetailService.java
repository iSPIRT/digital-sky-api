package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.BasicApplication;
import com.ispirit.digitalsky.document.ImportDroneApplication;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplication;
import com.ispirit.digitalsky.domain.AccountVerificationEmail;
import com.ispirit.digitalsky.domain.ResetPasswordEmail;
import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.repository.UserRepository;
import com.ispirit.digitalsky.service.api.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

public class CustomUserDetailService implements UserService {

    private UserRepository userRepository;
    private EmailService emailService;
    private DroneAcquisitionApplicationService<LocalDroneAcquisitionApplication> localDroneService;
    private DroneAcquisitionApplicationService<ImportDroneApplication> importDroneService;
    private UAOPApplicationService uaopApplicationService;
    private UINApplicationService uinApplicationService;
    private String resetPasswordBasePath;
    private String accountVerificationBasePath;

    public CustomUserDetailService(
            UserRepository userRepository,
            EmailService emailService,
            DroneAcquisitionApplicationService<LocalDroneAcquisitionApplication> localDroneService,
            DroneAcquisitionApplicationService<ImportDroneApplication> importDroneService,
            UAOPApplicationService uaopApplicationService,
            UINApplicationService uinApplicationService,
            String resetPasswordBasePath,
            String accountVerificationBasePath) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.localDroneService = localDroneService;
        this.importDroneService = importDroneService;
        this.uaopApplicationService = uaopApplicationService;
        this.uinApplicationService = uinApplicationService;
        this.resetPasswordBasePath = resetPasswordBasePath;
        this.accountVerificationBasePath = accountVerificationBasePath;
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
    public User find(long id) {
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
    public void sendEmailVerificationLink(User user) {
        String token = UUID.randomUUID().toString();

        User userEntity = userRepository.findOne(user.getId());
        userEntity.setAccountVerificationToken(token);
        userRepository.save(userEntity);

        String accountVerificationLink = format("%s?token=%s", accountVerificationBasePath, token);
        emailService.send(new AccountVerificationEmail(user.getEmail(), accountVerificationLink));
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

    @Override
    public User loadByEmail(String email) {
        return userRepository.loadByEmail(email);
    }

    @Override
    @Transactional
    public User createNew(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<BasicApplication> applications(long userId) {
        ArrayList<BasicApplication> basicApplications = new ArrayList<>();
        basicApplications.addAll(localDroneService.getApplicationsOfApplicant());
        basicApplications.addAll(importDroneService.getApplicationsOfApplicant());
        basicApplications.addAll(uaopApplicationService.getApplicationsOfApplicant(userId));
        basicApplications.addAll(uinApplicationService.getApplicationsOfApplicant(userId));
        basicApplications.sort((BasicApplication a1, BasicApplication a2) -> a2.modifiedDate().compareTo(a1.modifiedDate()));
        return basicApplications;
    }

    @Override
    public void verifyAccount(String token) {
        User user = userRepository.loadByAccountVerificationToken(token);
        if (user == null) {
            throw new EntityNotFoundException("AccountVerificationToken", token);
        }
        user.setAccountVerified(true);
        userRepository.save(user);
    }

}
