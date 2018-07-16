package com.ispirit.digitalsky.domain;

import javax.persistence.*;


public class PersonAddress {


    private long id;

    private String type;

    private Address address;

    private PersonAddress() {
    }

    public PersonAddress(String type, Address address) {
        this.type = type;
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public Address getAddress() {
        return address;
    }
}
