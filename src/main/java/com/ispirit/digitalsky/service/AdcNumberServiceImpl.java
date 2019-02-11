package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.AdcNumber;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.repository.AdcNumberRepository;
import com.ispirit.digitalsky.service.api.AdcNumberService;

import java.time.LocalDate;

public class AdcNumberServiceImpl implements AdcNumberService {

  private AdcNumberRepository adcNumberRepository;

  private FlyDronePermissionApplicationServiceImpl flyDronePermissionApplicationService;

  public AdcNumberServiceImpl(AdcNumberRepository adcNumberRepository,FlyDronePermissionApplicationServiceImpl flyDronePermissionApplicationService) {
    this.adcNumberRepository = adcNumberRepository;
    this.flyDronePermissionApplicationService = flyDronePermissionApplicationService;
  }

  @Override
  public String generateNewAdcNumber(LocalDate validFor, FlyDronePermissionApplication dronePermissionApplication) {
    AdcNumber latestEntryForDate = adcNumberRepository.loadLatestAdcNumberOfDate(validFor);
    if(latestEntryForDate==null){
      AdcNumber newAdcNumber = new AdcNumber(dronePermissionApplication.getFir(),"A",0,1,dronePermissionApplication.getId(),flyDronePermissionApplicationService);
      adcNumberRepository.save(newAdcNumber);
      return newAdcNumber.getAdcNumber();
    }
    try {
      latestEntryForDate.incrementSeventhNumber();
    }
    catch (RuntimeException e){
      throw new RuntimeException("Issue with increment in ADC",e);
    }
    adcNumberRepository.save(latestEntryForDate);
    return latestEntryForDate.getAdcNumber();
  }

  @Override
  public AdcNumber getDetailsOfAdcNumber(long id) {
    AdcNumber adcId = find(id);
    return adcId;
  }

  @Override
  public AdcNumber find(long id) {
    AdcNumber adcNumber = adcNumberRepository.findOne(id);
    if (adcNumber == null) {
      throw new EntityNotFoundException("AirspaceCategory", id);
    }
    return adcNumber;
  }
}
