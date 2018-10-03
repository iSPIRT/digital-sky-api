package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ispirit.digitalsky.util.BusinessIdentifierGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ds_organization_operator")
public class OrganizationOperator extends Organisation {
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

    private OrganizationOperator() {
        //for serialization and de-serialization
        businessIdentifier = BusinessIdentifierGenerator.generateOrganizationOperatorBusinessIdentifier();
    }

    public OrganizationOperator(long resourceOwnerId, String status, String name, String email, String mobileNumber, String contactNumber, String country, List<Address> addressList) {
        this();
        this.status = status;
        this.resourceOwnerId = resourceOwnerId;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.contactNumber = contactNumber;
        this.country = country;
        this.addressList = addressList;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getResourceOwnerId() {
        return resourceOwnerId;
    }

    public void setResourceOwnerId(long resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
    }

    public String getStatus() {
        return status;
    }

    public String getBusinessIdentifier() { return businessIdentifier; }
}
