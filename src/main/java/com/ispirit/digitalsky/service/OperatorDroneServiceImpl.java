package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.OperatorDrone;

import com.ispirit.digitalsky.repository.OperatorDroneRepository;
import com.ispirit.digitalsky.service.api.OperatorDroneService;

import java.util.ArrayList;
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
    public OperatorDrone updateOperatorDrone(OperatorDrone operatorDrone) {
        return operatorDroneRepository.save(operatorDrone);
    }

    @Override
    public OperatorDrone find(long id) {
        return operatorDroneRepository.findOne(id);
    }

    @Override
    public List<?> loadByOperator(long operatorId, ApplicantType operatorType) {
        return operatorDroneRepository.loadByOperator(operatorId, operatorType);
    }

}
