package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;

public interface AdcNumberService {

  String generateNewAdcNumber(FlyDronePermissionApplication dronePermissionApplication);

//  AdcNumber getDetailsOfAdcNumber(String adcNumber);

}