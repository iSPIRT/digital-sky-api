package com.ispirit.digitalsky.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ispirit.digitalsky.util.BusinessIdentifierGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ds_individual_operator")
public class IndividualOperator extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "RESOURCE_OWNER_ID")
    @JsonIgnore
    private long resourceOwnerId;


    @Column(name = "STATUS")
    @JsonIgnore
    private String status = "DEFAULT";

    @Column(name = "BUSINESS_IDENTIFIER")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String businessIdentifier;

    private IndividualOperator() {
        //for serialization and de-serialization
        businessIdentifier = BusinessIdentifierGenerator.generateIndividualOperatorBusinessIdentifier();
    }

    public IndividualOperator(long resourceOwnerId, String status, String name, String email, String mobileNumber, String country, LocalDate dateOfBirth, List<Address> addressList) {
        this();
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

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessIdentifier() { return businessIdentifier; }
}
