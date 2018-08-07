package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ds_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @Column(name = "TYPE")
    private String type = "DEFAULT";

    @Column(name = "LINE_ONE")
    @NotNull
    private String lineOne;

    @Column(name = "LINE_TWO")
    private String lineTwo;

    @Column(name = "TOWN_OR_CITY")
    @NotNull
    private String city;

    @Column(name = "STATE")
    @NotNull
    private String state;

    @Column(name = "COUNTRY")
    @NotNull
    private String country;

    @Column(name = "PIN_CODE")
    @NotNull
    @Size(max = 7)
    private String pinCode;

    private Address() {
    }

    public Address(String type, String lineOne, String lineTwo, String city, String state, String country, String pinCode) {
        this.lineOne = lineOne;
        this.lineTwo = lineTwo;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLineOne() {
        return lineOne;
    }

    public void setLineOne(String lineOne) {
        this.lineOne = lineOne;
    }

    public String getLineTwo() {
        return lineTwo;
    }

    public void setLineTwo(String lineTwo) {
        this.lineTwo = lineTwo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}
