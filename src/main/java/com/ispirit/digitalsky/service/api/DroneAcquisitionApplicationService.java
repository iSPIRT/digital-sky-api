package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.exception.*;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface DroneAcquisitionApplicationService<T> {

    T createDroneAcquisitionApplication(T droneAcquisitionApplicationForm);

    T updateDroneAcquisitionApplication(String id, T droneAcquisitionApplicationForm, MultipartFile securityClearanceDoc) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException, ApplicationNotEditableException;

    T approveDroneAcquisitionApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException,UnAuthorizedAccessException;

    T get(String id);

    Collection<T> getApplicationsOfApplicant();

    Collection<T> getAllApplications();

    Resource getFile(String id, String fileName) throws StorageFileNotFoundException, UnAuthorizedAccessException;

}
