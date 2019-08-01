package com.ispirit.digitalsky.document;

import java.util.ArrayList;

public class FlightLog {
  private String permissionArtefact;
  private String previousLogHash;
  private ArrayList<LogEntries> logEntries;

  public FlightLog(String permissionArtefact, String previousLogHash, ArrayList<LogEntries> logEntries) {
    this.permissionArtefact = permissionArtefact;
    this.previousLogHash = previousLogHash;
    this.logEntries = logEntries;
  }

  private FlightLog() {
  }

  public String getPreviousLogHash() {
    return previousLogHash;
  }

}