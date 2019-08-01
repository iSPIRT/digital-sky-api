package com.ispirit.digitalsky.document;

import java.time.LocalDateTime;

public class LogEntries {
  private EntryType entryType;
  private double timeStamp;
  private double longitude;
  private double latitude;
  private double altitude;
  private int crc;

  public LogEntries(EntryType entryType, double timeStamp, double longitude, double latitude, double altitude, int crc) {
    this.entryType = entryType;
    this.timeStamp = timeStamp;
    this.longitude = longitude;
    this.latitude = latitude;
    this.altitude = altitude;
    this.crc = crc;
  }

  private LogEntries() {
  }

}