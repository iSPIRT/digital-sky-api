package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.OperatorDrone;

import com.ispirit.digitalsky.domain.OperatorDroneStatus;
import com.ispirit.digitalsky.repository.OperatorDroneRepository;
import com.ispirit.digitalsky.service.api.OperatorDroneService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OperatorDroneServiceImpl implements OperatorDroneService {

    private OperatorDroneRepository operatorDroneRepository ;

    public OperatorDroneServiceImpl(OperatorDroneRepository operatorDroneRepository) {

        this.operatorDroneRepository = operatorDroneRepository;
    }

    @Override
    public List<OperatorDrone> createOperatorDrones(List<OperatorDrone> operatorDrones) {
        Iterable<OperatorDrone> addedDrones =  operatorDroneRepository.save(operatorDrones);
        List<OperatorDrone> dronesList = new ArrayList<>();
        addedDrones.forEach(dronesList::add);
        return dronesList;
    }

    @Override
    public OperatorDrone updateOperatorDrone(long id, String uinApplicationId, OperatorDroneStatus droneStatus ) {
        OperatorDrone actualDrone = operatorDroneRepository.findOne(id);
        actualDrone.setUinApplicationId(uinApplicationId);
        actualDrone.setOperatorDroneStatus(droneStatus);

        return operatorDroneRepository.save(actualDrone);
    }

    @Override
    public OperatorDrone find(long id) {
        return operatorDroneRepository.findOne(id);
    }

    @Override
    public List<?> loadByOperator(long operatorId, ApplicantType operatorType) {
        return operatorDroneRepository.loadByOperator(operatorId, operatorType);
    }

    @Override
    public OperatorDrone updateOperatorDroneStatus(long id, OperatorDroneStatus operatorDroneStatus) {
        OperatorDrone drone = operatorDroneRepository.findOne(id);
        drone.setOperatorDroneStatus(operatorDroneStatus);

        if(operatorDroneStatus == OperatorDroneStatus.UIN_APPROVED) {
            drone.setRegisteredDate(LocalDate.now());
        }

        return operatorDroneRepository.save(drone);
    }

}
