package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.*;

import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ValidationException;
import com.ispirit.digitalsky.repository.OperatorDroneRepository;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UserProfileService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class OperatorDroneServiceImpl implements OperatorDroneService {

    private final OperatorDroneRepository operatorDroneRepository ;
    private final UserProfileService userProfileService;

    public OperatorDroneServiceImpl(OperatorDroneRepository operatorDroneRepository, UserProfileService userProfileService) {
        this.operatorDroneRepository = operatorDroneRepository;
        this.userProfileService = userProfileService;
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
    public List<OperatorDrone> loadByOperator() throws ValidationException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        long userId = userPrincipal.getId();

        long operatorId;
        ApplicantType operatorType;
        UserProfile userProfile = userProfileService.profile(userId);

        if (userProfile.isIndividualOperator()) {
            operatorId = userProfile.getIndividualOperatorId();
            operatorType = ApplicantType.INDIVIDUAL;
        } else if (userProfile.isOrganizationOperator()) {
            operatorId = userProfile.getOrgOperatorId();
            operatorType = ApplicantType.ORGANISATION;
        } else {
            throw new ValidationException(new Errors("Applicant not operator"));
        }

        return operatorDroneRepository.loadByOperator(operatorId, operatorType);
    }

    @Override
    public OperatorDrone updateStatus(long id, OperatorDroneStatus operatorDroneStatus) {
        OperatorDrone drone = operatorDroneRepository.findOne(id);
        drone.setOperatorDroneStatus(operatorDroneStatus);

        if(operatorDroneStatus == OperatorDroneStatus.UIN_APPROVED) {
            drone.setRegisteredDate(LocalDate.now());
            drone.setApprovalTimestamp(new Timestamp(new Date().getTime()));
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
                .anyMatch(opDrone -> (opDrone.getUinApplicationId() != null
                        && !opDrone.getUinApplicationId().equals(uinId)
                        && opDrone.getDeviceId() != null  &&
                        opDrone.getDeviceId().equals(deviceUniqueDeviceId)
                ));
        return anyMatchExists;
    }

    @Override
    public void createUinNumberForDevice(long id){
        OperatorDrone drone = operatorDroneRepository.findOne(id);
        long operatorDroneid =operatorDroneRepository.findLatestUIN().get(0);
        OperatorDrone lastdrone = operatorDroneRepository.findOne(operatorDroneid);
        if(lastdrone==null || lastdrone.getUinNo()==null) {
            drone.setUinNo("U"+String.format("%07d", 1));
            operatorDroneRepository.save(drone);
            return;
        }
        else{
            int currentInt = Integer.parseInt(lastdrone.getUinNo().substring(1));
            if(currentInt+1>9999999)
                throw new RuntimeException("Out of bounds for UIN number");
            drone.setUinNo("U"+String.format("%07d",currentInt+1));
            operatorDroneRepository.save(drone);
            return;
        }
    }

}
