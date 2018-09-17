package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.DroneDeviceRegistrationStatus;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.DroneDeviceRepository;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.ManufacturerRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.DroneDeviceService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.SignatureVerifierService;

import java.time.LocalDate;
import java.util.Collection;

public class DroneDeviceServiceImpl implements DroneDeviceService {

    private DroneDeviceRepository droneDeviceRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private OperatorDroneService operatorDroneService;
    private SignatureVerifierService signatureVerifierService;
    private ManufacturerRepository manufacturerRepository;

    public DroneDeviceServiceImpl(DroneDeviceRepository droneDeviceRepository,
                                  SignatureVerifierService signatureVerifierService,
                                  IndividualOperatorRepository individualOperatorRepository,
                                  OrganizationOperatorRepository organizationOperatorRepository,
                                  ManufacturerRepository manufacturerRepository,
                                  OperatorDroneService operatorDroneService) {
        this.droneDeviceRepository = droneDeviceRepository;
        this.signatureVerifierService = signatureVerifierService;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
        this.operatorDroneService = operatorDroneService;
        this.manufacturerRepository = manufacturerRepository;
    }

    @Override
    public DroneDevice register(String manufacturerId, RegisterDroneRequestPayload payload) throws InvalidOperatorCodeException, DroneDeviceAlreadyExistException, InvalidDigitalSignatureException {
        DroneDevice drone = payload.getDrone();
        if(signatureVerifierService.isValidSignature(payload)) {
            if(!droneExists(drone.getDeviceId())) {
                if(operatorExists(drone.getOperatorCode())) {
                    if(manufacturerExists(manufacturerId)) {
                        drone.setCreatedDate(LocalDate.now());
                        drone.setManufacturerId(manufacturerId);
                        drone.setRegistrationStatus(DroneDeviceRegistrationStatus.REGISTERED);
                        DroneDevice createdDrone = droneDeviceRepository.save(drone);
                        return createdDrone;
                    }
                    else {
                        throw new ManufacturerIdInvalidException();
                    }
                }
                else {
                    throw new InvalidOperatorCodeException();
                }
            }
            else {
                throw new DroneDeviceAlreadyExistException();
            }
        }
        else {
            throw new InvalidDigitalSignatureException();
        }
    }

    @Override
    public DroneDevice deregister(String manufacturerId, RegisterDroneRequestPayload payload) throws DroneDeviceNotFoundException, InvalidDigitalSignatureException {
        if(signatureVerifierService.isValidSignature(payload)) {
            DroneDevice actualDrone = droneDeviceRepository.findByDeviceId(payload.getDrone().getDeviceId());
            if (actualDrone == null) {
                throw new DroneDeviceNotFoundException();
            }
            actualDrone.setLastModifiedDate(LocalDate.now());
            actualDrone.setRegistrationStatus(DroneDeviceRegistrationStatus.DEREGISTERED);
            DroneDevice savedDrone = droneDeviceRepository.save(actualDrone);
            return savedDrone;
        }
        else {
            throw new InvalidDigitalSignatureException();
        }
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

    private boolean manufacturerExists(String manufacturerId) {
        Long id = Long.parseLong(manufacturerId);
        boolean manufacturerExists = manufacturerRepository.findOne(id) != null;
        return manufacturerExists;
    }

}
