package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "uaopApplications")
@TypeAlias("uaopApplication")
public class UAOPApplication extends BasicApplication {

    @Field("name")
    @NotNull
    private String name;

    @Field("designation")
    @NotNull
    private String designation;

    @Field("securityProgramDocName")
    private String securityProgramDocName;

    @JsonIgnore
    @Transient
    private MultipartFile securityProgramDoc;


    @Field("landOwnerPermissionDocName")
    private String landOwnerPermissionDocName;

    @JsonIgnore
    @Transient
    private MultipartFile landOwnerPermissionDoc;

    @Field("insuranceDocName")
    private String insuranceDocName;

    @JsonIgnore
    @Transient
    private MultipartFile insuranceDoc;

    @Field("sopDocName")
    private String sopDocName;

    @JsonIgnore
    @Transient
    private MultipartFile sopDoc;

    public UAOPApplication() {
        setCreatedDate(new Date());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSecurityProgramDocName() {
        return securityProgramDocName;
    }

    public void setSecurityProgramDocName(String securityProgramDocName) {
        this.securityProgramDocName = securityProgramDocName;
    }

    public String getLandOwnerPermissionDocName() {
        return landOwnerPermissionDocName;
    }

    public void setLandOwnerPermissionDocName(String landOwnerPermissionDocName) {
        this.landOwnerPermissionDocName = landOwnerPermissionDocName;
    }

    public String getInsuranceDocName() {
        return insuranceDocName;
    }

    public void setInsuranceDocName(String insuranceDocName) {
        this.insuranceDocName = insuranceDocName;
    }

    public String getSopDocName() {
        return sopDocName;
    }

    public void setSopDocName(String sopDocName) {
        this.sopDocName = sopDocName;
    }

    public MultipartFile getSecurityProgramDoc() {
        return securityProgramDoc;
    }

    public void setSecurityProgramDoc(MultipartFile securityProgramDoc) {
        this.securityProgramDoc = securityProgramDoc;
    }

    public MultipartFile getLandOwnerPermissionDoc() {
        return landOwnerPermissionDoc;
    }

    public void setLandOwnerPermissionDoc(MultipartFile landOwnerPermissionDoc) {
        this.landOwnerPermissionDoc = landOwnerPermissionDoc;
    }

    public MultipartFile getInsuranceDoc() {
        return insuranceDoc;
    }

    public void setInsuranceDoc(MultipartFile insuranceDoc) {
        this.insuranceDoc = insuranceDoc;
    }

    public MultipartFile getSopDoc() {
        return sopDoc;
    }

    public void setSopDoc(MultipartFile sopDoc) {
        this.sopDoc = sopDoc;
    }

    public List<MultipartFile> getAllDocs() {
        ArrayList<MultipartFile> list = new ArrayList<>();
        if (securityProgramDoc != null && !securityProgramDoc.isEmpty()) {
            list.add(securityProgramDoc);
        }
        if (sopDoc != null && !sopDoc.isEmpty()) {
            list.add(sopDoc);
        }
        if (insuranceDoc != null && !insuranceDoc.isEmpty()) {
            list.add(insuranceDoc);
        }
        if (landOwnerPermissionDoc != null && !landOwnerPermissionDoc.isEmpty()) {
            list.add(landOwnerPermissionDoc);
        }
        return list;
    }
}
