package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.exception.*;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface DroneAcquisitionApplicationService<T> {

    T createDroneAcquisitionApplicationForm(T droneAcquisitionApplicationForm);

    T updateDroneAcquisitionApplicationForm(String id, T droneAcquisitionApplicationForm, MultipartFile securityClearanceDoc) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException, ApplicationNotEditableException;

    T approveDroneAcquisitionForm(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException,UnAuthorizedAccessException;

    T get(String id);

    Collection<T> getAcquisitionFormsOfApplicant(long applicantId);

    Collection<T> getAllAcquisitionForms();

    Resource getFile(String id, String fileName) throws StorageFileNotFoundException;

}
