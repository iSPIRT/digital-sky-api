package com.ispirit.digitalsky.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Entity
@Table(name = "DS_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "FULL_NAME")
    private String fullName;


    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD_HASH")
    private String password;

    @Column(name = "RESET_PASSWORD_TOKEN")
    private String resetPasswordToken;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private List<UserRole> roles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private List<UserActiveSession> activeSessions = new ArrayList<>();

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

    public List<String> getRoleNames(){
        if(roles==null || roles.isEmpty()) return emptyList();
        return  roles.stream().map(UserRole::getUserRole).collect(toList());
    }
}
