package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.*;

import com.ispirit.digitalsky.exception.OperatorNotAuthorizedException;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OperatorDroneRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.OperatorDroneService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class OperatorDroneServiceImpl implements OperatorDroneService {

    private OperatorDroneRepository operatorDroneRepository ;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;

    public OperatorDroneServiceImpl(OperatorDroneRepository operatorDroneRepository, IndividualOperatorRepository individualOperatorRepository, OrganizationOperatorRepository organizationOperatorRepository) {

        this.operatorDroneRepository = operatorDroneRepository;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
    }

    @Override
    public List<OperatorDrone> createOperatorDrones(List<OperatorDrone> operatorDrones) {
        Iterable<OperatorDrone> addedDrones =  operatorDroneRepository.save(operatorDrones);
        List<OperatorDrone> dronesList = new ArrayList<>();
        addedDrones.forEach(dronesList::add);
        return dronesList;
    }

    @Override
    public OperatorDrone updateUINApplicationId(long id, String uinApplicationId, OperatorDroneStatus droneStatus ) {
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
    public List<OperatorDrone> loadByOperator() throws OperatorNotAuthorizedException{
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        long userId = userPrincipal.getId();
        long operatorId = 0 ;
        ApplicantType operatorType = ApplicantType.ORGANISATION;
        IndividualOperator individualOperator = individualOperatorRepository.loadByResourceOwner(userId);
        if(individualOperator != null) {
            operatorId = individualOperator.getId();
            operatorType = ApplicantType.INDIVIDUAL;
        }
        else {
            OrganizationOperator organizationOperator =  organizationOperatorRepository.loadByResourceOwner(userId);
            if(organizationOperator !=null ) {
                operatorId = organizationOperator.getId();
            }
        }
        if(operatorId != 0) {
            return operatorDroneRepository.loadByOperator(operatorId, operatorType);
        } else return null;
    }

    @Override
    public OperatorDrone updateStatus(long id, OperatorDroneStatus operatorDroneStatus) {
        OperatorDrone drone = operatorDroneRepository.findOne(id);
        drone.setOperatorDroneStatus(operatorDroneStatus);


        if(operatorDroneStatus == OperatorDroneStatus.UIN_APPROVED) {
            drone.setRegisteredDate(LocalDate.now());
        }

        return operatorDroneRepository.save(drone);
    }

    @Override
    public OperatorDrone updateUniqueDeviceId(long id, String uniqueDeviceCode) {
        OperatorDrone drone = operatorDroneRepository.findOne(id);
        drone.setDeviceId(uniqueDeviceCode);

        return operatorDroneRepository.save(drone);
    }

    @Override
    public Collection<String> getAvailableDroneDeviceIds(Collection<String> droneDeviceIds) {
        List<OperatorDrone> operatorDrones = loadByOperator();
        List<String> availableDroneDeviceIds =  new ArrayList<>();

        for(String deviceId : droneDeviceIds) {
            boolean isDeviceTaken = false;
            for (OperatorDrone operatorDrone : operatorDrones) {
                if(deviceId.equals(operatorDrone.getDeviceId())) {
                    isDeviceTaken = true;
                    break;
                }
            }
            if(!isDeviceTaken) {
                availableDroneDeviceIds.add(deviceId);
            }
        }

        return availableDroneDeviceIds;
    }

    @Override
    public boolean isMappedToDifferentUIN(String deviceUniqueDeviceId, String uinId, long operatorId, ApplicantType applicantType) {
        List<OperatorDrone> operatorDrones = operatorDroneRepository.loadByOperator(operatorId, applicantType );
        boolean anyMatchExists =  operatorDrones.stream()
                .anyMatch(opDrone -> (opDrone.getUinApplicationId() != null && !opDrone.getUinApplicationId().equals(uinId) && opDrone.getDeviceId() != null  && opDrone.getDeviceId().equals(deviceUniqueDeviceId)));
        return anyMatchExists;
    }

}
