package com.ispirit.digitalsky.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.ispirit.digitalsky.util.BusinessIdentifierGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @JsonIgnore
    private String droneCategory;

    @Transient
    @Size(min = 1, max = 5)
    private List<DroneCategoryType> droneCategoryTypes = new ArrayList<>();

    @Column(name = "TRAINING_CERTIFICATE_DOC_NAME")
    @Size(max = 100)
    private String trainingCertificateDocName;

    @JsonIgnore
    @Transient
    private MultipartFile trainingCertificate;

    @Column(name = "BUSINESS_IDENTIFIER")
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

    public Pilot(long resourceOwnerId, String status, String name, String email, String mobileNumber, String country, LocalDate dateOfBirth, List<DroneCategoryType> droneCategoryList, List<Address> addressList) {
        this();
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.country = country;
        this.dateOfBirth = dateOfBirth;
        this.addressList = addressList;
        this.resourceOwnerId = resourceOwnerId;
        this.status = status;
        this.droneCategoryTypes.addAll(droneCategoryList);
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

    public void setTrainingCertificateDocName(String trainingCertificateDocName) {
        this.trainingCertificateDocName = trainingCertificateDocName;
    }

    public void setTrainingCertificate(MultipartFile trainingCertificate) {
        this.trainingCertificate = trainingCertificate;
    }

    public String getBusinessIdentifier() {
        return businessIdentifier;
    }

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

    public void resolveDroneCategoryFromList() {
        droneCategory = droneCategoryTypes.stream().map(DroneCategoryType::getValue).collect(Collectors.joining(";"));
    }

    public List<DroneCategoryType> getDroneCategoryTypes() {
        return droneCategoryTypes;
    }

    public String getDroneCategory() {
        return droneCategory;
    }

    public void resolveResolveListFromDroneCategory() {
        if (StringUtils.hasText(droneCategory)) {
            String[] tokens = droneCategory.split(";");
            for (String token : tokens) {
                droneCategoryTypes.add(DroneCategoryType.fromValue(token));
            }
        }
    }

    //for tests only
    public void setDroneCategory(String droneCategory) {
        this.droneCategory = droneCategory;
    }
}
