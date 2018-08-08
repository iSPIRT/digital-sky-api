package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneType;
import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.OperatorDroneStatus;
import com.ispirit.digitalsky.repository.OperatorDroneRepository;
import com.ispirit.digitalsky.service.api.OperatorDroneService;

public class OperatorDroneServiceImpl implements OperatorDroneService {

    private OperatorDroneRepository operatorDroneRepository ;

    public OperatorDroneServiceImpl(OperatorDroneRepository operatorDroneRepository) {

        this.operatorDroneRepository = operatorDroneRepository;
    }

    @Override
    public OperatorDrone createNewOperatorDrone(long droneTypeId, String acquisitionApplicationId, boolean isImported) {
        return null;
    }

    @Override
    public DroneType updateOperatorDrone(long id, long uinApplicationId, OperatorDroneStatus operatorDroneStatus) {
        return null;
    }

    @Override
    public DroneType find(long id) {
        return null;
    }
}
