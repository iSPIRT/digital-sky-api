package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Drone;
import com.ispirit.digitalsky.domain.DroneRegistrationStatus;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.DroneAlreadyExistException;
import com.ispirit.digitalsky.exception.DroneNotFoundException;
import com.ispirit.digitalsky.exception.InvalidDigitalSignatureException;
import com.ispirit.digitalsky.exception.InvalidOperatorCodeException;
import com.ispirit.digitalsky.repository.DroneRepository;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.DroneService;
import com.ispirit.digitalsky.service.api.SignatureVerifierService;

import java.time.LocalDate;
import java.util.Collection;

public class DroneServiceImpl implements DroneService {

    private DroneRepository droneRegistrationRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private SignatureVerifierService signatureVerifierService;

    public DroneServiceImpl(DroneRepository droneRegistrationRepository,
                            SignatureVerifierService signatureVerifierService,
                            IndividualOperatorRepository individualOperatorRepository,
                            OrganizationOperatorRepository organizationOperatorRepository) {
        this.droneRegistrationRepository = droneRegistrationRepository;
        this.signatureVerifierService = signatureVerifierService;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
    }

    @Override
    public Drone register(String manufacturerId, RegisterDroneRequestPayload payload) throws InvalidOperatorCodeException, DroneAlreadyExistException, InvalidDigitalSignatureException {
        Drone drone = payload.getDrone();
        if(signatureVerifierService.isValidSignature(payload)) {
            if(!droneExists(drone.getDeviceId())) {
                if(operatorExists(drone.getOperatorCode())) {
                    drone.setCreatedDate(LocalDate.now());
                    drone.setManufacturerId(manufacturerId);
                    drone.setRegistrationStatus(DroneRegistrationStatus.REGISTERED);
                    Drone createdDrone = droneRegistrationRepository.save(drone);
                    return createdDrone;
                }
                else {
                    throw new InvalidOperatorCodeException();
                }
            }
            else {
                throw new DroneAlreadyExistException();
            }
        }
        else {
            throw new InvalidDigitalSignatureException();
        }
    }

    @Override
    public Drone deregister(String manufacturerId, RegisterDroneRequestPayload payload) throws DroneNotFoundException, InvalidDigitalSignatureException {
        if(signatureVerifierService.isValidSignature(payload)) {
            Drone actualDrone = droneRegistrationRepository.findByDeviceId(payload.getDrone().getDeviceId());
            if (actualDrone == null) {
                throw new DroneNotFoundException();
            }
            actualDrone.setLastModifiedDate(LocalDate.now());
            actualDrone.setRegistrationStatus(DroneRegistrationStatus.DEREGISTERED);
            Drone savedDrone = droneRegistrationRepository.save(actualDrone);
            return savedDrone;
        }
        else {
            throw new InvalidDigitalSignatureException();
        }
    }

    @Override
    public Collection<Drone> getOperatorDrones(String operatorCode) {
        return droneRegistrationRepository.findOperatorDrones(operatorCode);
    }

    private boolean droneExists(String uniqueDeviceCode) {
        Drone drone = droneRegistrationRepository.findByDeviceId(uniqueDeviceCode) ;
        boolean droneExists = (drone != null) ? true : false ;
        return droneExists;
    }

    private boolean operatorExists(String operatorCode) {
        Long operatorId = Long.parseLong(operatorCode);
        boolean operatorExists = individualOperatorRepository.findOne(operatorId) != null  || organizationOperatorRepository.findOne(operatorId) != null;
        return operatorExists;
    }

}
