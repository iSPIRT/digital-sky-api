package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.OperatorDroneStatus;

import java.util.Collection;
import java.util.List;

public interface OperatorDroneService {

    Collection<OperatorDrone> createOperatorDrones(List<OperatorDrone> operatorDrones);

    OperatorDrone updateUINApplicationId(long id, String uinApplicationId, OperatorDroneStatus operatorDroneStatus);

    OperatorDrone find(long id);

    Collection<?> loadByOperator();

    OperatorDrone updateStatus(long id, OperatorDroneStatus operatorDroneStatus);

    OperatorDrone updateUniqueDeviceId(long id, String uniqueOperatorCode);
    
    Collection<String> getAvailableDroneDeviceIds(Collection<String> droneDeviceIds);

}
