package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Entity
@Table(name = "ds_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "FULL_NAME")
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9 ]*$")
    private String fullName;

    @Column(name = "EMAIL")
    @NotNull
    @Email
    private String email;

    @Column(name = "PASSWORD_HASH")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 7, max = 50)
    private String password;

    @Column(name = "RESET_PASSWORD_TOKEN")
    @JsonIgnore
    private String resetPasswordToken;

    @Column(name = "ACCOUNT_VERIFICATION_TOKEN")
    @JsonIgnore
    private String accountVerificationToken;

    @Column(name = "ACCOUNT_VERIFIED")
    @JsonIgnore
    private boolean accountVerified = false;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private List<UserRole> roles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonIgnore
    private List<UserActiveSession> activeSessions = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    @NotNull
    private String reCaptcha;

    private User() {
        //for serialization and de-serialization
    }

    public User(long id, String fullName, String email, String password, List<UserRole> roles) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public List<UserActiveSession> getActiveSessions() {
        return activeSessions;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public void setAccountVerificationToken(String accountVerificationToken) {
        this.accountVerificationToken = accountVerificationToken;
    }

    public void setAccountVerified(boolean accountVerified) {
        this.accountVerified = accountVerified;
    }

    public boolean isAccountVerified() {
        return accountVerified;
    }

    public String getReCaptcha() {
        return reCaptcha;
    }

    public List<String> getRoleNames() {
        if (roles == null || roles.isEmpty()) return emptyList();
        return roles.stream().map(UserRole::getUserRole).collect(toList());
    }

    public String getAccountVerificationToken() {
        return accountVerificationToken;
    }

    public void setReCaptcha(String reCaptcha) {
        this.reCaptcha = reCaptcha;
    }
}
