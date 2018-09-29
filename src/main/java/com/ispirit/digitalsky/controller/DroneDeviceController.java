package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.service.api.DroneDeviceService;

import com.ispirit.digitalsky.service.api.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.ispirit.digitalsky.controller.DroneDeviceController.DRONEDEVICE_RESOURCE_BASE_PATH;


@RestController
@RequestMapping(DRONEDEVICE_RESOURCE_BASE_PATH)
public class DroneDeviceController {
    public static final String DRONEDEVICE_RESOURCE_BASE_PATH = "/api/droneDevice";

    DroneDeviceService droneDeviceService;
    UserProfileService userProfileService;

    public DroneDeviceController(DroneDeviceService droneDeviceService, UserProfileService userProfileService) {
        this.droneDeviceService = droneDeviceService;
        this.userProfileService = userProfileService;
    }

    @RequestMapping(value = "/register/{manufacturerBusinessIdentifier}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerDrone(@PathVariable String manufacturerBusinessIdentifier, @Valid @RequestBody RegisterDroneRequestPayload payload) {
        RegisterDroneResponsePayload responsePayload = new RegisterDroneResponsePayload(payload.getDrone().getTxn(), LocalDateTime.now());
        try {
            droneDeviceService.register(manufacturerBusinessIdentifier, payload);
            responsePayload.setCode(RegisterDroneResponseCode.REGISTERED);
            return new ResponseEntity<>(responsePayload, HttpStatus.CREATED);
        } catch (InvalidDigitalSignatureException e) {
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_SIGNATURE);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(OperatorCodeMissingException e) {
            responsePayload.setCode(RegisterDroneResponseCode.OPERATOR_CODE_MISSING);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        }   catch(InvalidOperatorCodeException e){
            responsePayload.setCode(RegisterDroneResponseCode.OPERATOR_CODE_INVALID);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(DroneDeviceAlreadyExistException e) {
            responsePayload.setCode(RegisterDroneResponseCode.DRONE_ALREADY_REGISTERED);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(InvalidDigitalCertificateException e) {
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_DIGITAL_CERTIFICATE);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(InvalidManufacturerException e){
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_MANUFACTURER);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(Exception e){
            responsePayload.setCode(RegisterDroneResponseCode.BAD_REQUEST_PAYLOAD);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/deregister/{manufacturerBusinessIdentifier}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deregisterDrone(@PathVariable String manufacturerBusinessIdentifier, @Valid @RequestBody RegisterDroneRequestPayload payload) {
        RegisterDroneResponsePayload responsePayload = new RegisterDroneResponsePayload(payload.getDrone().getTxn(), LocalDateTime.now());
        try {
            droneDeviceService.deregister(manufacturerBusinessIdentifier, payload);
            responsePayload.setCode(RegisterDroneResponseCode.DEREGISTERED);
            return new ResponseEntity<>(responsePayload, HttpStatus.OK);
        } catch(InvalidDigitalSignatureException e){
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_SIGNATURE);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(InvalidDigitalCertificateException e) {
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_DIGITAL_CERTIFICATE);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        }  catch(DroneDeviceNotFoundException e){
            responsePayload.setCode(RegisterDroneResponseCode.DRONE_NOT_FOUND);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(InvalidManufacturerException e){
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_MANUFACTURER);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(Exception e){
            responsePayload.setCode(RegisterDroneResponseCode.BAD_REQUEST_PAYLOAD);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());

        if(profile == null ) {throw new InvalidOperatorCodeException(); }

        Collection<String> operatorDrones= droneDeviceService.getRegisteredDroneDeviceIds(profile.getOperatorBusinessIdentifier());
        return new ResponseEntity<>(operatorDrones, HttpStatus.OK);
    }
}

