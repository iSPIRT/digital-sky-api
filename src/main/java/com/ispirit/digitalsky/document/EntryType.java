package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum EntryType {
  @JsonProperty("GEOFENCE_BREACH")GEO_FENCE_BREACH("GEOFENCE_BREACH"), @JsonProperty("TAKEOFF/ARM")TAKEOFF_OR_ARM("TAKEOFF/ARM"), @JsonProperty("TIME_BREACH")TIME_BREACH("TIME_BREACH"), @JsonProperty("LAND/DISARM")LAND_OR_DISARM("LAND/DISARM");

  private String value;
  private EntryType(String value) {
    this.value = value;
  }

  public static EntryType fromValue(String value) {
    for (EntryType entryType : values()) {
      if (entryType.value.equalsIgnoreCase(value)) {
        return entryType;
      }
    }
    throw new IllegalArgumentException(
        "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
  }
}