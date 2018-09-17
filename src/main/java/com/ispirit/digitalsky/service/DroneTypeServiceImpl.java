package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneType;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.repository.DroneTypeRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.DroneTypeService;

import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DroneTypeServiceImpl implements DroneTypeService {

    private final StorageService storageService;
    private final DroneTypeRepository droneTypeRepository;

    public DroneTypeServiceImpl(DroneTypeRepository droneTypeRepository, StorageService storageService) {

        this.droneTypeRepository = droneTypeRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public DroneType createDroneType(DroneType droneType) {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        droneType.setCreatedBy(userPrincipal.getId());
        droneType.setLastModifiedBy(userPrincipal.getId());
        droneType.setLastModifiedDate(LocalDate.now());
        DroneType savedDroneType = droneTypeRepository.save(droneType);
        storageService.store(droneType.getAllDocs(), String.valueOf(savedDroneType.getId()));

        return savedDroneType;
    }

    @Override
    @Transactional
    public DroneType updateDroneType(long id, DroneType droneType) {
        DroneType actualDroneType = droneTypeRepository.findOne(id);
        UserPrincipal userPrincipal = UserPrincipal.securityContext();

        if (actualDroneType == null) {
            throw new RuntimeException("Drone Type Not Found");
        }

        long createdById = actualDroneType.getCreatedBy();
        LocalDate createdDate = actualDroneType.getCreatedDate();

        if (actualDroneType.getOpManualDoc() != null) {
            actualDroneType.setOpManualDocName(actualDroneType.getOpManualDocName());
        }

        if (actualDroneType.getMaintenanceGuidelinesDoc() != null) {
            actualDroneType.setMaintenanceGuidelinesDocName(actualDroneType.getMaintenanceGuidelinesDocName());
        }

        BeanUtils.copyProperties(droneType, actualDroneType);

        actualDroneType.setLastModifiedDate(LocalDate.now());
        actualDroneType.setLastModifiedBy(userPrincipal.getId());
        actualDroneType.setCreatedDate(createdDate);
        actualDroneType.setCreatedBy(createdById);

        DroneType savedDroneType = droneTypeRepository.save(actualDroneType);

        storageService.store(savedDroneType.getAllDocs(), String.valueOf(savedDroneType.getId()));

        return savedDroneType;
    }

    @Override
    public Collection<DroneType> getAll() {

        List<DroneType> droneTypes = new ArrayList<DroneType>();
        Iterable<DroneType> allDroneTypes = droneTypeRepository.findAll();
        allDroneTypes.forEach(droneTypes::add);

        return droneTypes;
    }

    @Override
    public DroneType get(long id) {

        return droneTypeRepository.findOne(id);
    }

    public Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException {
        return storageService.loadAsResource(applicationId, fileName);
    }
}
