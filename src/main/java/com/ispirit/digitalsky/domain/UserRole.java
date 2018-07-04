package com.ispirit.digitalsky.domain;

import javax.persistence.*;

@Entity
@Table(name = "DS_USER_ROLE")
public class UserRole {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private long id;

    @Column(name = "USER_ROLE")
    private String userRole;

    private UserRole() {
        //for serialization and de-serialization
    }

    public UserRole(long id, String userRole) {
        this.id = id;
        this.userRole = userRole;
    }

    public long getId() {
        return id;
    }

    public String getUserRole() {
        return userRole;
    }
}
