package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.DroneAcquisitionApplicationForm;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.exception.ApplicationFormNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface DroneAcquisitionApplicationFormService<T> {

    T createDroneAcquisitionApplicationForm(T droneAcquisitionApplicationForm);

    T updateDroneAcquisitionApplicationForm(String id, T droneAcquisitionApplicationForm, MultipartFile securityClearanceDoc) throws ApplicationFormNotFoundException, UnAuthorizedAccessException, StorageException;

    T approveDroneAcquisitionForm(ApproveRequestBody approveRequestBody) throws ApplicationFormNotFoundException,UnAuthorizedAccessException;

    T get(String id);

    Collection<T> getAcquisitionFormsOfApplicant(long applicantId);

    Collection<T> getAllAcquisitionForms();

    Resource getFile(String id, String fileName) throws StorageFileNotFoundException;

}
