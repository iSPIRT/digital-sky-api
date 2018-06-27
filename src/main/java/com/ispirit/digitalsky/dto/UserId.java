package com.ispirit.digitalsky.dto;

import com.ispirit.digitalsky.domain.User;

public class UserId {

    private long id;

    private UserId() {
        //for serialization and de-serialization
    }

    public UserId(User user) {
        this.id = user.getId();
    }

    public long getId() {
        return id;
    }
}
