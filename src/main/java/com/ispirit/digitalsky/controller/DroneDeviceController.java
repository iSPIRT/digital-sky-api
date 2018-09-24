package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.domain.RegisterDroneResponseCode;
import com.ispirit.digitalsky.domain.RegisterDroneResponsePayload;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.DroneDeviceAlreadyExistException;
import com.ispirit.digitalsky.exception.DroneDeviceNotFoundException;
import com.ispirit.digitalsky.exception.InvalidDigitalSignatureException;
import com.ispirit.digitalsky.exception.InvalidOperatorCodeException;
import com.ispirit.digitalsky.service.api.DroneDeviceService;

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

    public DroneDeviceController(DroneDeviceService droneDeviceService) {
        this.droneDeviceService = droneDeviceService;
    }

    @RequestMapping(value = "/register/{manufacturerId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerDrone(@PathVariable String manufacturerId, @Valid @RequestBody RegisterDroneRequestPayload payload) {
        RegisterDroneResponsePayload responsePayload = new RegisterDroneResponsePayload(payload.getDrone().getTxn(), LocalDateTime.now());
        try {
            droneDeviceService.register(manufacturerId, payload);
            responsePayload.setCode(RegisterDroneResponseCode.REGISTERED);
            return new ResponseEntity<>(responsePayload, HttpStatus.CREATED);
        } catch(InvalidDigitalSignatureException e){
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_SIGNATURE);
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(InvalidOperatorCodeException e){
            responsePayload.setCode(RegisterDroneResponseCode.OPERATORCODE_INVALID);
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(DroneDeviceAlreadyExistException e){
            responsePayload.setCode(RegisterDroneResponseCode.DRONE_ALREADY_REGISTERED);
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(Exception e){
            responsePayload.setCode(RegisterDroneResponseCode.BAD_REQUEST_PAYLOAD);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/deregister/{manufacturerId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deregisterDrone(@PathVariable String manufacturerId, @Valid @RequestBody RegisterDroneRequestPayload payload) {
        RegisterDroneResponsePayload responsePayload = new RegisterDroneResponsePayload(payload.getDrone().getTxn(), LocalDateTime.now());
        try {
            droneDeviceService.deregister(manufacturerId, payload);
            responsePayload.setCode(RegisterDroneResponseCode.DEREGISTERED);
            return new ResponseEntity<>(responsePayload, HttpStatus.OK);
        } catch(InvalidDigitalSignatureException e){
            responsePayload.setCode(RegisterDroneResponseCode.INVALID_SIGNATURE);
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(DroneDeviceNotFoundException e){
            responsePayload.setCode(RegisterDroneResponseCode.DRONE_NOT_FOUND);
            return new ResponseEntity<>(responsePayload, HttpStatus.PRECONDITION_FAILED);
        } catch(Exception e){
            responsePayload.setCode(RegisterDroneResponseCode.BAD_REQUEST_PAYLOAD);
            responsePayload.setError(new Errors(e.getMessage()));
            return new ResponseEntity<>(responsePayload, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(@RequestParam(value = "operatorCode", required = true) String operatorCode) {
        Collection<String> operatorDrones= droneDeviceService.getRegisteredDroneDeviceIds(operatorCode);
        return new ResponseEntity<>(operatorDrones, HttpStatus.OK);
    }
}

