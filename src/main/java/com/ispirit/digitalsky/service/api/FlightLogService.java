package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.FlightLogEntry;
import com.ispirit.digitalsky.exception.StorageException;

public interface FlightLogService {

  void storeFlightLog(FlyDronePermissionApplication application, String content, FlightLogEntry flightLogEntry) throws StorageException;

  boolean testAgainstPreviousHash(FlightLogEntry flightLogEntry);

}
