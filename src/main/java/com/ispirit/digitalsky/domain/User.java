package com.ispirit.digitalsky.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany
    @JoinColumn(name = "USER_ID")
    private List<UserRole> roles = new ArrayList<>();

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
}
