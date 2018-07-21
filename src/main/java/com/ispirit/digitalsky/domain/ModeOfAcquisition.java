package com.ispirit.digitalsky.domain;

import java.util.Arrays;

public enum ModeOfAcquisition {
    LEASE("LEASE"), PURCHASE("PURCHASE");

    private String value;
    private ModeOfAcquisition(String value) {
        this.value = value;
    }

    public static ModeOfAcquisition fromValue(String value) {
        for (ModeOfAcquisition modeOfAcquisition : values()) {
            if (modeOfAcquisition.value.equalsIgnoreCase(value)) {
                return modeOfAcquisition;
            }
        }
        throw new IllegalArgumentException(
                String.format("Unknown enum type %s, Allowed values are %s", value, Arrays.toString(values())));
    }
}
