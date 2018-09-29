package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.DroneDeviceAlreadyExistException;
import com.ispirit.digitalsky.exception.DroneDeviceNotFoundException;
import com.ispirit.digitalsky.exception.InvalidDigitalSignatureException;
import com.ispirit.digitalsky.exception.InvalidOperatorBusinessIdentifierException;

import java.util.Collection;

public interface DroneDeviceService {
    DroneDevice register(String manufacturerId, RegisterDroneRequestPayload drone) throws InvalidOperatorBusinessIdentifierException, DroneDeviceAlreadyExistException, InvalidDigitalSignatureException;
    DroneDevice deregister(String manufacturerId, RegisterDroneRequestPayload drone) throws DroneDeviceNotFoundException, InvalidDigitalSignatureException;
    Collection<String> getRegisteredDroneDeviceIds(String operatorCode);
}
