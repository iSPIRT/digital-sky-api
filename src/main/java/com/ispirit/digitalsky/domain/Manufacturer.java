package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ds_manufacturer")
public class Manufacturer extends Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "RESOURCE_OWNER_ID")
    @JsonIgnore
    private long resourceOwnerId;


    @Column(name = "STATUS")
    @JsonIgnore
    private String status = "DEFAULT";

    @Column(name = "TRUSTED_CERTIFICATE_DOC_NAME")
    private String trustedCertificateDocName;

    @JsonIgnore
    @Transient
    private MultipartFile trustedCertificateDoc;

    private Manufacturer() {
        //for serialization and de-serialization
    }

    public Manufacturer(long resourceOwnerId, String status, String name, String email, String mobileNumber, String contactNumber, String country, List<Address> addressList) {
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

    public String getTrustedCertificateDocName() { return trustedCertificateDocName; }

    public void setTrustedCertificateDocName(String trustedCertificateDocName) { this.trustedCertificateDocName = trustedCertificateDocName; }

    public MultipartFile getTrustedCertificateDoc() { return trustedCertificateDoc; }

    public void setTrustedCertificateDoc(MultipartFile trustedCertificateDoc) { this.trustedCertificateDoc = trustedCertificateDoc; }
}
