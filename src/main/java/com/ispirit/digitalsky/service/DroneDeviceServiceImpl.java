package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.DroneDeviceRepository;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.DroneDeviceService;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.DigitalSignatureVerifierService;
import org.springframework.transaction.annotation.Transactional;

import java.security.SignatureException;
import java.time.LocalDate;
import java.util.Collection;

public class DroneDeviceServiceImpl implements DroneDeviceService {

    private DroneDeviceRepository droneDeviceRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private OperatorDroneService operatorDroneService;
    private DigitalSignatureVerifierService signatureVerifierService;
    private ManufacturerService manufacturerService;

    public DroneDeviceServiceImpl(DroneDeviceRepository droneDeviceRepository,
                                  DigitalSignatureVerifierService signatureVerifierService,
                                  IndividualOperatorRepository individualOperatorRepository,
                                  OrganizationOperatorRepository organizationOperatorRepository,
                                  OperatorDroneService operatorDroneService,
                                  ManufacturerService manufacturerService) {
        this.droneDeviceRepository = droneDeviceRepository;
        this.signatureVerifierService = signatureVerifierService;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
        this.operatorDroneService = operatorDroneService;
        this.manufacturerService = manufacturerService;
    }

    @Override
    @Transactional
    public DroneDevice register(String manufacturerBusinessIdentifier, RegisterDroneRequestPayload payload) throws InvalidOperatorBusinessIdentifierException, DroneDeviceAlreadyExistException, InvalidDigitalSignatureException, InvalidManufacturerException, ManufacturerNotFoundException, InvalidDigitalCertificateException, OperatorBusinessIdentifierMissingException {
        Manufacturer manufacturer = manufacturerService.loadByBusinessIdentifier(manufacturerBusinessIdentifier);

        if( manufacturer == null) { throw new ManufacturerNotFoundException(); }

        String trustedCertificatePath = manufacturerService.getCAAndTrustedCertificatePath(manufacturer.getId());
        try {
            if (!signatureVerifierService.isValidSignature(payload, manufacturer.getName(), trustedCertificatePath)) {
                throw new InvalidDigitalSignatureException();
            }
        } catch (SignatureException e) {
            throw new InvalidDigitalSignatureException();
        }
        DroneDevice drone = payload.getDrone();
        if (droneExists(drone.getDeviceId())) { throw new DroneDeviceAlreadyExistException(); }
        if(drone.getOperatorBusinessIdentifier() == null) { throw new OperatorBusinessIdentifierMissingException();}
        if (operatorExists(drone.getOperatorBusinessIdentifier())) {
            drone.setCreatedDate(LocalDate.now());
            drone.setManufacturerBusinessIdentifier(manufacturerBusinessIdentifier);
            drone.setRegistrationStatus(DroneDeviceRegistrationStatus.REGISTERED);
            DroneDevice createdDrone = droneDeviceRepository.save(drone);
            return createdDrone;
        } else {
            throw new InvalidOperatorBusinessIdentifierException();
        }
    }

    @Override
    @Transactional
    public DroneDevice deregister(String manufacturerBusinessIdentifier, RegisterDroneRequestPayload payload) throws DroneDeviceNotFoundException, ManufacturerNotFoundException, DeviceNotInRegisteredStateException, InvalidDigitalSignatureException {
        Manufacturer manufacturer = manufacturerService.loadByBusinessIdentifier(manufacturerBusinessIdentifier);

        if( manufacturer == null) { throw new ManufacturerNotFoundException(); }

        String trustedCertificatePath = manufacturerService.getCAAndTrustedCertificatePath(manufacturer.getId());

        try {
            if (!signatureVerifierService.isValidSignature(payload, manufacturer.getName(), trustedCertificatePath)) {  throw new InvalidDigitalSignatureException(); }
        } catch (SignatureException e) {
            throw new InvalidDigitalSignatureException();
        }

        DroneDevice actualDrone = droneDeviceRepository.findByDeviceId(payload.getDrone().getDeviceId());
        if (actualDrone == null) { throw new DroneDeviceNotFoundException(); }
        if( actualDrone.getRegistrationStatus() != DroneDeviceRegistrationStatus.REGISTERED ) { throw new DeviceNotInRegisteredStateException(); }

        actualDrone.setLastModifiedDate(LocalDate.now());
        actualDrone.setRegistrationStatus(DroneDeviceRegistrationStatus.DEREGISTERED);
        DroneDevice savedDrone = droneDeviceRepository.save(actualDrone);
        return savedDrone;
    }

    @Override
    public Collection<String> getRegisteredDroneDeviceIds(String operatorBusinessIdentifier) {
        Collection<String> droneDeviceIds =  droneDeviceRepository.findRegisteredDroneDeviceIds(operatorBusinessIdentifier);
        return operatorDroneService.getAvailableDroneDeviceIds(droneDeviceIds);
    }

    private boolean droneExists(String uniqueDeviceCode) {
        DroneDevice drone = droneDeviceRepository.findByDeviceId(uniqueDeviceCode) ;
        boolean droneExists = (drone != null) ? true : false ;
        return droneExists;
    }

    private boolean operatorExists(String operatorCode) {
        boolean operatorExists = individualOperatorRepository.loadByBusinessIdentifier(operatorCode) != null  || organizationOperatorRepository.loadByBusinessIdentifier(operatorCode) != null;
        return operatorExists;
    }

}
