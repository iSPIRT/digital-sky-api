package com.ispirit.digitalsky.domain;

import java.util.Arrays;

public enum  ApplicantCategory {
    EXISTING_UAOP_HOLDER("EXISTING_UAOP_HOLDER"), UAOP_APPLICANT("UAOP_APPLICANT"), WITHOUT_UAOP("WITHOUT_UAOP");

    private String value;
    private ApplicantCategory(String value) {
        this.value = value;
    }

    public static ApplicantCategory fromValue(String value) {
        for (ApplicantCategory applicationCategory : values()) {
            if (applicationCategory.value.equalsIgnoreCase(value)) {
                return applicationCategory;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
