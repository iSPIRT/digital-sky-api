package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.DroneType;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import org.springframework.core.io.Resource;

import java.util.Collection;

public interface DroneService {

    DroneType createDroneType(DroneType droneType);

    DroneType updateDroneType(long id, DroneType droneType);

    Collection<DroneType> getAll();

    DroneType get(long id);

    Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException;
}
