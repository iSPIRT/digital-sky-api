package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.OperatorDroneStatus;

import java.util.List;

public interface OperatorDroneService {

    List<OperatorDrone> createOperatorDrones(List<OperatorDrone> operatorDrones);

    OperatorDrone updateOperatorDrone(long id, String uinApplicationId, OperatorDroneStatus operatorDroneStatus);

    OperatorDrone find(long id);

    List<?> loadByOperator(long userId, ApplicantType applicantType);

    OperatorDrone updateOperatorDroneStatus(long id, OperatorDroneStatus operatorDroneStatus);
}
