package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ds_organization")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Organisation {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    protected long id;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "EMAIL")
    protected String email;

    @Column(name = "MOBILE_NUMBER")
    protected String mobileNumber;

    @Column(name = "CONTACT_NUMBER")
    protected String contactNumber;

    @Column(name = "COUNTRY")
    protected String country;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ds_organization_address",
            joinColumns = @JoinColumn(name = "ORGANIZATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "ADDRESS_ID")
    )
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

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCountry() {
        return country;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

}
