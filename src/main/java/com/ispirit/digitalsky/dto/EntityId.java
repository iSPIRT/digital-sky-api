package com.ispirit.digitalsky.dto;

import com.ispirit.digitalsky.domain.User;

public class EntityId {

    private long id;

    private EntityId() {
        //for serialization and de-serialization
    }

    public EntityId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
