package com.ispirit.digitalsky.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.util.CustomLocalDateDeSerializer;
import com.ispirit.digitalsky.util.CustomLocalDateSerializer;
import com.ispirit.digitalsky.util.LocalDateAttributeConverter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ds_person")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    protected long id;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "EMAIL")
    protected String email;

    @Column(name = "MOBILE_NUMBER")
    @NotNull
    @Size(max = 13)
    protected String mobileNumber;

    @Column(name = "COUNTRY")
    protected String country;

    @Column(name = "DATE_OF_BIRTH")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @Convert(converter = LocalDateAttributeConverter.class)
    @NotNull
    protected LocalDate dateOfBirth;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ds_person_address",
            joinColumns = @JoinColumn(name = "PERSON_ID"),
            inverseJoinColumns = @JoinColumn(name = "ADDRESS_ID")
    )
    @Valid
    protected List<Address> addressList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getCountry() {
        return country;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }
}