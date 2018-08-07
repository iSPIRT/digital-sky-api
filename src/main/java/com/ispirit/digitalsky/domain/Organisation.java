package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @NotNull
    protected String name;

    @Column(name = "EMAIL")
    @NotNull
    @Email
    protected String email;

    @Column(name = "MOBILE_NUMBER")
    @NotNull
    @Size(max = 13)
    protected String mobileNumber;

    @Column(name = "CONTACT_NUMBER")
    @Size(max = 13)
    protected String contactNumber;

    @Column(name = "COUNTRY")
    @NotNull
    @Size(max = 20)
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
