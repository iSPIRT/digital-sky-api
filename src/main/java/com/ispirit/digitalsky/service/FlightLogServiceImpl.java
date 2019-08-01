package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.FlightLogEntry;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.repository.FlightLogRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.FlightLogService;

public class FlightLogServiceImpl implements FlightLogService {

  public static final String FLIGHT_LOG_JSON = "flightLog.json";

  private StorageService storageService;

  private FlightLogRepository repository;

  public FlightLogServiceImpl(FlightLogRepository repository, StorageService storageService) {
    this.repository = repository;
    this.storageService = storageService;
  }

  @Override
  public void storeFlightLog(FlyDronePermissionApplication application, String content, FlightLogEntry flightLogEntry) throws StorageException {
    try{
      storageService.loadAsResource(application.getId(), FLIGHT_LOG_JSON);
      throw new RuntimeException("Flight log already exists.");
    }
    catch (StorageFileNotFoundException e) {
      repository.save(flightLogEntry);
      storageService.store(FLIGHT_LOG_JSON, content, application.getId());
    }
  }

  @Override
  public boolean testAgainstPreviousHash(FlightLogEntry flightLogEntry) {
    if (!repository.checkEntriesForUin(flightLogEntry.getUin()))
      return true;
    long id = repository.getLatestIdForUin(flightLogEntry.getUin());
    return repository.getHashForId(id).equals(flightLogEntry.getSignature());
  }

}
