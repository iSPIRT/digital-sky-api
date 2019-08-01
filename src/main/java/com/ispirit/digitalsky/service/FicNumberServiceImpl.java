package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.FicNumber;
import com.ispirit.digitalsky.repository.FicNumberRepository;
import com.ispirit.digitalsky.service.api.FicNumberService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public class FicNumberServiceImpl implements FicNumberService {

  private FicNumberRepository ficNumberRepository;

  public FicNumberServiceImpl(FicNumberRepository ficNumberRepository) {
    this.ficNumberRepository = ficNumberRepository;
  }

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  @Override
  public String generateNewFicNumber(FlyDronePermissionApplication dronePermissionApplication) {
    LocalDateTime startDateTime = dronePermissionApplication.getStartDateTime();
    long count = ficNumberRepository.findNumberOfEntriesForDate(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 0, 0));
    if (count == 0) {
      FicNumber newFicNumber = new FicNumber(Character.toString(dronePermissionApplication.getFir().charAt(0)).toUpperCase(), 0, dronePermissionApplication.getId(), LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 0, 0));
      return ficNumberRepository.save(newFicNumber).getFicNumber();
    }

    long id = ficNumberRepository.loadLatestFicNumberOfDate(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 0, 0));
    FicNumber latestEntryForDate = ficNumberRepository.findOne(id);
    try {
      latestEntryForDate.incrementNum();
    } catch (RuntimeException e) {
      throw new RuntimeException("Issue with increment in FIC", e);
    }
    return ficNumberRepository.save(latestEntryForDate).getFicNumber();

  }
}
