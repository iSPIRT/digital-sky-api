package com.ispirit.digitalsky.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_user_active_session")
public class UserActiveSession {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private long id;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "TIMESTAMP")
    private LocalDateTime validity;

    private UserActiveSession() {
        //for serialization and de-serialization
    }

    public UserActiveSession(String token, LocalDateTime validity) {
        this.token = token;
        this.validity = validity;
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getValidity() {
        return validity;
    }
}
