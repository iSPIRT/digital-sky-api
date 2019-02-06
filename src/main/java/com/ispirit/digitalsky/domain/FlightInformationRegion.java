package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.GeoJsonObject;

import java.io.IOException;

public class FlightInformationRegion {
  private long id;

  private String name;

  private GeoJsonObject geoJson;

  private String geoJsonString;

  private char representingCharacter;


  private FlightInformationRegion() {
    //for serialization and de-serialization
  }

  public FlightInformationRegion(String name, String geoJsonString,char representingCharacter) {
    this.name = name;
    this.representingCharacter = representingCharacter;
    this.geoJsonString = geoJsonString;
    try {
      this.geoJson = new ObjectMapper().readValue(geoJsonString, GeoJsonObject.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getGeoJsonString() {
    return geoJsonString;
  }

  public GeoJsonObject getGeoJson() {
    return geoJson;
  }

  public void setName(String name) {
    this.name = name;
  }

  public char getRepresentingCharacter() {
    return representingCharacter;
  }

  public void setRepresentingCharacter(char representingCharacter) {
    this.representingCharacter = representingCharacter;
  }

}
