package com.ispirit.digitalsky.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ds_director")
public class Director extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "STATUS")
    @JsonIgnore
    private String status = "DEFAULT";


    @Column(name = "ORGANIZATION_ID")
    @JsonIgnore
    private long organisationId;


    private Director() {
        //for serialization and de-serialization
    }

    public Director(long organisationId, String status, String name, String email, String mobileNumber, String country, LocalDate dateOfBirth, List<Address> addressList) {
        this.organisationId = organisationId;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.addressList = addressList;
        this.status = status;

    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setOrganisationId(long organisationId) {
        this.organisationId = organisationId;
    }
}
