package com.ispirit.digitalsky.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class DroneType {

    @Id
    private String id;
    private String modelName;
    private Date modelYear;
    private String modelNumber;
    private DroneCategory category;
    private List<String> technical_specs;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Date getModelYear() {
        return modelYear;
    }

    public void setModelYear(Date modelYear) {
        this.modelYear = modelYear;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public DroneCategory getCategory() {
        return category;
    }

    public void setCategory(DroneCategory category) {
        this.category = category;
    }

    public List<String> getTechnical_specs() {
        return technical_specs;
    }

    public void setTechnical_specs(List<String> technical_specs) {
        this.technical_specs = technical_specs;
    }
}
