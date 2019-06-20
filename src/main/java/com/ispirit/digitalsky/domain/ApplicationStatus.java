package com.ispirit.digitalsky.domain;

import java.util.Arrays;

public enum ApplicationStatus {
    DRAFT("DRAFT"),SUBMITTED("SUBMITTED"),APPROVED("APPROVED"),REJECTED("REJECTED"),APPROVEDBYATC("APPROVEDBYATC"),APPROVEDBYAFMLU("APPROVEDBYAFMLU"),REJECTEDBYAFMLU("REJECTEDBYAFMLU"),REJECTEDBYATC("REJECTEDBYATC");

    private String value;
    private ApplicationStatus(String value) {
        this.value = value;
    }

    public static ApplicationStatus fromValue(String value) {
        for (ApplicationStatus applicationStatus : values()) {
            if (applicationStatus.value.equalsIgnoreCase(value)) {
                return applicationStatus;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
