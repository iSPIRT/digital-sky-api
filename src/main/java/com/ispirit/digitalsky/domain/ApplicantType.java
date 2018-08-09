package com.ispirit.digitalsky.domain;

import java.util.Arrays;

public enum ApplicantType {
    INDIVIDUAL("INDIVIDUAL"), ORGANISATION("ORGANISATION"), GOVERNMENT("GOVERNMENT");

    private String value;
    private ApplicantType(String value) {
        this.value = value;
    }

    public static ApplicantType fromValue(String value) {
        for (ApplicantType applicantType : values()) {
            if (applicantType.value.equalsIgnoreCase(value)) {
                return applicantType;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
};
