package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.DroneType;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import org.springframework.core.io.Resource;

import java.util.Collection;

public interface DroneTypeService {

    DroneType createDroneType(DroneType droneType) throws StorageException;

    DroneType updateDroneType(long id, DroneType droneType) throws EntityNotFoundException, StorageException;

    Collection<DroneType> getAll();

    DroneType get(long id);

    Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException;
}
