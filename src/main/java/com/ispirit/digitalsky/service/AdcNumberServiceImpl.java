package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.AdcNumber;
import com.ispirit.digitalsky.repository.AdcNumberRepository;
import com.ispirit.digitalsky.service.api.AdcNumberService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public class AdcNumberServiceImpl implements AdcNumberService {

  private AdcNumberRepository adcNumberRepository;

  public AdcNumberServiceImpl(AdcNumberRepository adcNumberRepository) {
    this.adcNumberRepository = adcNumberRepository;
  }

  @Transactional(value = Transactional.TxType.REQUIRES_NEW)
  @Override
  public String generateNewAdcNumber(FlyDronePermissionApplication dronePermissionApplication) {
    LocalDateTime startDateTime = dronePermissionApplication.getStartDateTime();
    long count = adcNumberRepository.findNumberOfEntriesForDate(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 0, 0));
    if (count == 0) {
      AdcNumber newAdcNumber = new AdcNumber(Character.toString(dronePermissionApplication.getFir().charAt(0)).toUpperCase(), "A", 0, 1, dronePermissionApplication.getId(), LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 0, 0));
      return adcNumberRepository.save(newAdcNumber).getAdcNumber();
    }

    long id = adcNumberRepository.loadLatestAdcNumberOfDate(LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonthValue(), startDateTime.getDayOfMonth(), 0, 0));
    AdcNumber latestEntryForDate = adcNumberRepository.findOne(id);
    try {
      latestEntryForDate.incrementSeventhNumber();
    } catch (RuntimeException e) {
      throw new RuntimeException("Issue with increment in ADC", e);
    }
    return adcNumberRepository.save(latestEntryForDate).getAdcNumber();

  }

}
