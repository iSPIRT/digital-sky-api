package com.ispirit.digitalsky.document;

public class FlightLogObject {

  private String signature;

  private FlightLog flightLog;

  public FlightLogObject(String signature, FlightLog flightLog) {
    this.signature = signature;
    this.flightLog = flightLog;
  }

  private FlightLogObject() {
  }

  public String getPreviousFlightLogHash() {
    return flightLog.getPreviousLogHash();
  }

}
