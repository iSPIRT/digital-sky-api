package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.DroneType;
import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.OperatorDroneStatus;

public interface OperatorDroneService {

    OperatorDrone createNewOperatorDrone(long droneTypeId, String acquisitionApplicationId, boolean isImported);

    DroneType updateOperatorDrone(long id, long uinApplicationId, OperatorDroneStatus operatorDroneStatus);

    DroneType find(long id);
}
