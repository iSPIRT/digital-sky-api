package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.Drone;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.DroneAlreadyExistException;
import com.ispirit.digitalsky.exception.DroneNotFoundException;
import com.ispirit.digitalsky.exception.InvalidDigitalSignatureException;
import com.ispirit.digitalsky.exception.InvalidOperatorCodeException;

import java.util.Collection;

public interface DroneService {
    Drone register(String manufacturerId, RegisterDroneRequestPayload drone) throws InvalidOperatorCodeException, DroneAlreadyExistException, InvalidDigitalSignatureException;
    Drone deregister(String manufacturerId, RegisterDroneRequestPayload drone) throws DroneNotFoundException, InvalidDigitalSignatureException;
    Collection<Drone> getOperatorDrones(String operatorCode);
}
