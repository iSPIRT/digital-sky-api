package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.AdcNumber;

import java.time.LocalDate;

public interface AdcNumberService {

  String generateNewAdcNumber(FlyDronePermissionApplication dronePermissionApplication);

//  AdcNumber getDetailsOfAdcNumber(long adcNumber);

}