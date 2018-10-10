package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.service.api.DroneDeviceService;

import com.ispirit.digitalsky.service.api.UserProfileService;

import com.ispirit.digitalsky.util.CustomValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

import static com.ispirit.digitalsky.controller.DroneDeviceController.DRONEDEVICE_RESOURCE_BASE_PATH;


@RestController
@RequestMapping(DRONEDEVICE_RESOURCE_BASE_PATH)
public class DroneDeviceController {
    public static final String DRONEDEVICE_RESOURCE_BASE_PATH = "/api/droneDevice";


    private final DroneDeviceService droneDeviceService;
    private final UserProfileService userProfileService;
    private final CustomValidator validator;

    public DroneDeviceController(DroneDeviceService droneDeviceService, UserProfileService userProfileService, CustomValidator validator) {
        this.droneDeviceService = droneDeviceService;
        this.userProfileService = userProfileService;
        this.validator = validator;
    }

    @RequestMapping(value = "/register/{manufacturerBusinessIdentifier}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerDrone(@PathVariable String manufacturerBusinessIdentifier, @Valid @RequestBody RegisterDroneRequestPayload payload) {

        RegisterDroneResponsePayload responsePayload = new RegisterDroneResponsePayload();
        try {
            validator.validate(payload.getDrone());
            DroneDevice savedDevice = droneDeviceService.register(manufacturerBusinessIdentifier, payload);
            return (getResponseEntityForRegistration(responsePayload, RegisterDroneResponseCode.REGISTERED, savedDevice.getTxn()));
        } catch (ValidationException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.BAD_REQUEST_PAYLOAD, payload.getDrone().getTxn(), e.getMessage());
        } catch (InvalidDigitalSignatureException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.INVALID_SIGNATURE, payload.getDrone().getTxn(), e.getMessage());
        } catch (OperatorBusinessIdentifierMissingException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.OPERATOR_BUSINESS_IDENTIFIER_MISSING, payload.getDrone().getTxn(), e.getMessage());
        } catch (InvalidOperatorBusinessIdentifierException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.OPERATOR_BUSINESS_IDENTIFIER_INVALID, payload.getDrone().getTxn(), e.getMessage());
        } catch (DroneDeviceAlreadyExistException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.DRONE_ALREADY_REGISTERED, payload.getDrone().getTxn(), e.getMessage());
        } catch (InvalidDigitalCertificateException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.INVALID_DIGITAL_CERTIFICATE, payload.getDrone().getTxn(), e.getMessage());
        } catch (ManufacturerNotFoundException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.MANUFACTURER_BUSINESS_IDENTIFIER_INVALID, payload.getDrone().getTxn(), e.getMessage());
        } catch (InvalidManufacturerException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.INVALID_MANUFACTURER, payload.getDrone().getTxn(), e.getMessage());
        } catch (ManufacturerTrustedCertificateNotFoundException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.MANUFACTURER_TRUSTED_CERTIFICATE_NOT_FOUND, payload.getDrone().getTxn(), e.getMessage());
        } catch (Exception e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.BAD_REQUEST_PAYLOAD, payload.getDrone().getTxn(), e.getMessage());
        }
    }

    @RequestMapping(value = "/deregister/{manufacturerBusinessIdentifier}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deregisterDrone(@PathVariable String manufacturerBusinessIdentifier, @Valid @RequestBody RegisterDroneRequestPayload payload) {

        RegisterDroneResponsePayload responsePayload = new RegisterDroneResponsePayload();
        try {
            validator.validate(payload.getDrone());
            droneDeviceService.deregister(manufacturerBusinessIdentifier, payload);
            responsePayload.setResponseCode(RegisterDroneResponseCode.DEREGISTERED);
            responsePayload.setTxn(payload.getDrone().getTxn());
            return new ResponseEntity<>(responsePayload, HttpStatus.OK);
        } catch (DeviceNotInRegisteredStateException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.DRONE_NOT_REGISTERED, payload.getDrone().getTxn(), e.getMessage());
        } catch (InvalidDigitalSignatureException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.INVALID_SIGNATURE, payload.getDrone().getTxn(), e.getMessage());
        } catch (InvalidDigitalCertificateException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.INVALID_DIGITAL_CERTIFICATE, payload.getDrone().getTxn(), e.getMessage());
        } catch (DroneDeviceNotFoundException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.DRONE_NOT_FOUND, payload.getDrone().getTxn(), e.getMessage());
        } catch (ManufacturerNotFoundException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.MANUFACTURER_BUSINESS_IDENTIFIER_INVALID, payload.getDrone().getTxn(), e.getMessage());
        } catch (InvalidManufacturerException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.INVALID_MANUFACTURER, payload.getDrone().getTxn(), e.getMessage());
        } catch (ManufacturerTrustedCertificateNotFoundException e) {
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.MANUFACTURER_TRUSTED_CERTIFICATE_NOT_FOUND, payload.getDrone().getTxn(), e.getMessage());
        } catch(Exception e){
            return getResponseEntityForErrors(responsePayload, RegisterDroneResponseCode.BAD_REQUEST_PAYLOAD, payload.getDrone().getTxn(), e.getMessage());
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());

        if(profile == null ) {
            return new ResponseEntity<>(new Errors("Invalid operator business identifier"), HttpStatus.BAD_REQUEST);
        }
        if( !profile.isOperator() ) {
            return new ResponseEntity<>(new Errors("User not an operator"), HttpStatus.BAD_REQUEST);
        }

        Collection<String> operatorDrones = droneDeviceService.getRegisteredDroneDeviceIds(profile.getOperatorBusinessIdentifier());
        return new ResponseEntity<>(operatorDrones, HttpStatus.OK);
    }

    private ResponseEntity<?> getResponseEntityForRegistration(RegisterDroneResponsePayload payload, RegisterDroneResponseCode responseCode, String txn ) {
        payload.setResponseCode(responseCode);
        payload.setTxn(txn);
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
    }

    private ResponseEntity<?> getResponseEntityForErrors(RegisterDroneResponsePayload payload, RegisterDroneResponseCode responseCode, String txn, String errorMessage ) {
        payload.setResponseCode(responseCode);
        payload.setTxn(txn);
        payload.setError(new Errors(errorMessage));
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }
}

