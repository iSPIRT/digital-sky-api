package com.ispirit.digitalsky.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.ispirit.digitalsky.util.BusinessIdentifierGenerator;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ds_pilot")
public class Pilot extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "RESOURCE_OWNER_ID")
    @JsonIgnore
    private long resourceOwnerId;


    @Column(name = "STATUS")
    @JsonIgnore
    private String status = "DEFAULT";

    @Column(name = "DRONE_CATEGORY")
    @NotNull
    @Size(max = 15)
    private String droneCategory;

    @Column(name = "TRAINING_CERTIFICATE_DOC_NAME")
    @Size(max = 100)
    private String trainingCertificateDocName;

    @JsonIgnore
    @Transient
    private MultipartFile trainingCertificate;

    @Column(name = "BUSINESS_IDENTIFIER")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final String businessIdentifier;

    private Pilot() {
        //for serialization and de-serialization
        businessIdentifier = BusinessIdentifierGenerator.generatePilotBusinessIdentifier();
    }

    public Pilot(long id) {
        this();
        this.id = id;
    }


    public Pilot(long resourceOwnerId, String status, String name, String email, String mobileNumber, String country, LocalDate dateOfBirth, String droneCategory, List<Address> addressList) {
        this();
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.addressList = addressList;
        this.resourceOwnerId = resourceOwnerId;
        this.status = status;
        this.droneCategory = droneCategory;
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

    public void setTrainingCertificateDocName(String trainingCertificateDocName) { this.trainingCertificateDocName = trainingCertificateDocName; }

    public void setTrainingCertificate(MultipartFile trainingCertificate) { this.trainingCertificate = trainingCertificate; }

    public String getBusinessIdentifier() { return businessIdentifier; }

    public String getTrainingCertificateDocName() {
        return trainingCertificateDocName;
    }

    public MultipartFile getTrainingCertificate() {
        return trainingCertificate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
