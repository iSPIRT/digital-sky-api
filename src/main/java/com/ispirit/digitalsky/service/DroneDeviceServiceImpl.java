package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.DroneDeviceRegistrationStatus;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.DroneDeviceRepository;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.DroneDeviceService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.DigitalSignatureVerifierService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;

public class DroneDeviceServiceImpl implements DroneDeviceService {

    private DroneDeviceRepository droneDeviceRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private OperatorDroneService operatorDroneService;
    private DigitalSignatureVerifierService signatureVerifierService;

    public DroneDeviceServiceImpl(DroneDeviceRepository droneDeviceRepository,
                                  DigitalSignatureVerifierService signatureVerifierService,
                                  IndividualOperatorRepository individualOperatorRepository,
                                  OrganizationOperatorRepository organizationOperatorRepository,
                                  OperatorDroneService operatorDroneService) {
        this.droneDeviceRepository = droneDeviceRepository;
        this.signatureVerifierService = signatureVerifierService;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
        this.operatorDroneService = operatorDroneService;
    }

    @Override
    @Transactional
    public DroneDevice register(String manufacturerId, RegisterDroneRequestPayload payload) throws InvalidOperatorCodeException, DroneDeviceAlreadyExistException, InvalidDigitalSignatureException {
        if (!signatureVerifierService.isValidSignature(payload, Long.valueOf(manufacturerId))) {
            throw new InvalidDigitalSignatureException();
        }
        DroneDevice drone = payload.getDrone();
        if (droneExists(drone.getDeviceId())) { throw new DroneDeviceAlreadyExistException(); }
        if (operatorExists(drone.getOperatorCode())) {
            drone.setCreatedDate(LocalDate.now());
            drone.setManufacturerId(manufacturerId);
            drone.setRegistrationStatus(DroneDeviceRegistrationStatus.REGISTERED);
            DroneDevice createdDrone = droneDeviceRepository.save(drone);
            return createdDrone;
        } else {
            throw new InvalidOperatorCodeException();
        }
    }

    @Override
    @Transactional
    public DroneDevice deregister(String manufacturerId, RegisterDroneRequestPayload payload) throws DroneDeviceNotFoundException, InvalidDigitalSignatureException {
        if (!signatureVerifierService.isValidSignature(payload,Long.valueOf(manufacturerId))) {  throw new InvalidDigitalSignatureException(); }

        DroneDevice actualDrone = droneDeviceRepository.findByDeviceId(payload.getDrone().getDeviceId());
        if (actualDrone == null) { throw new DroneDeviceNotFoundException(); }

        actualDrone.setLastModifiedDate(LocalDate.now());
        actualDrone.setRegistrationStatus(DroneDeviceRegistrationStatus.DEREGISTERED);
        DroneDevice savedDrone = droneDeviceRepository.save(actualDrone);
        return savedDrone;
    }

    @Override
    public Collection<String> getRegisteredDroneDeviceIds(String operatorCode) {
        Collection<String> droneDeviceIds =  droneDeviceRepository.findRegisteredDroneDeviceIds(operatorCode);
        return operatorDroneService.getAvailableDroneDeviceIds(droneDeviceIds);
    }

    private boolean droneExists(String uniqueDeviceCode) {
        DroneDevice drone = droneDeviceRepository.findByDeviceId(uniqueDeviceCode) ;
        boolean droneExists = (drone != null) ? true : false ;
        return droneExists;
    }

    private boolean operatorExists(String operatorCode) {
        Long operatorId = Long.parseLong(operatorCode);
        boolean operatorExists = individualOperatorRepository.findOne(operatorId) != null  || organizationOperatorRepository.findOne(operatorId) != null;
        return operatorExists;
    }

}
