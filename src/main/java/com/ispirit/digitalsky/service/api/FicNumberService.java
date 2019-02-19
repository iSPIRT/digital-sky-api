package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;

public interface FicNumberService {

  String generateNewFicNumber(FlyDronePermissionApplication dronePermissionApplication);

  //  FicNumber getDetailsOfFicNumber(String adcNumber);

}
