package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.OperatorDrone;

import java.util.List;

public interface OperatorDroneService {

    List<OperatorDrone> createOperatorDrones(List<OperatorDrone> operatorDrones);

    OperatorDrone updateOperatorDrone(OperatorDrone operatorDrone);

    OperatorDrone find(long id);

    List<?> loadByOperator(long userId, ApplicantType applicantType);
}
