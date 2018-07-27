package com.ispirit.digitalsky.domain;

import java.util.Arrays;

public enum  DroneCategoryType {
    MICRO("MICRO"), SMALL("SMALL"), MEDIUM("MEDIUM"), LARGE("LARGE");

    private String value;
    private DroneCategoryType(String value) {
        this.value = value;
    }

    public static DroneCategoryType fromValue(String value) {
        for (DroneCategoryType droneCategoryType : values()) {
            if (droneCategoryType.value.equalsIgnoreCase(value)) {
                return droneCategoryType;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
