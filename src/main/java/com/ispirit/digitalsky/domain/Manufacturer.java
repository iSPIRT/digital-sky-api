package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Embeddable
@Table(name = "ds_manufacturer")
public class Manufacturer extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "RESOURCE_OWNER_ID")
    @JsonIgnore
    private long resourceOwnerId;


    @Column(name = "STATUS")
    @JsonIgnore
    private String status = "DEFAULT";


    private Manufacturer() {
        //for serialization and de-serialization
    }

    public Manufacturer(long resourceOwnerId, String status, String name, String email, String mobileNumber, String country, LocalDate dateOfBirth, List<Address> addressList) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.addressList = addressList;
        this.resourceOwnerId = resourceOwnerId;
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getResourceOwnerId() {
        return resourceOwnerId;
    }

    public String getStatus() {
        return status;
    }

    public void setResourceOwnerId(long resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
    }
}
