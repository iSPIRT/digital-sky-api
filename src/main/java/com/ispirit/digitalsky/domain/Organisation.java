package com.ispirit.digitalsky.domain;

import org.springframework.data.annotation.Id;

public class Organisation {

    @Id
    private String id;
    private String name;
    private String businessEmail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

}
