package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.exception.ApplicationNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import org.springframework.core.io.Resource;

import java.util.Collection;

public interface FlyDronePermissionApplicationService {

    FlyDronePermissionApplication createApplication(FlyDronePermissionApplication application);

    FlyDronePermissionApplication updateApplication(String id, FlyDronePermissionApplication application) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException;

    FlyDronePermissionApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException,UnAuthorizedAccessException;

    FlyDronePermissionApplication get(String id);

    Collection<FlyDronePermissionApplication> getApplicationsOfDrone(long droneId);

    Collection<FlyDronePermissionApplication> getAllApplications();

    Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException;

}
