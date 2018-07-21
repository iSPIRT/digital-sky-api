package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplicationForm;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.exception.ApplicationFormNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.Collection;

public interface LocalDroneAcquisitionApplicationFormService {

    LocalDroneAcquisitionApplicationForm createLocalDroneAcquisitionApplicationForm(LocalDroneAcquisitionApplicationForm localDroneAcquisitionApplicationForm);

    LocalDroneAcquisitionApplicationForm updateLocalDroneAcquisitionApplicationForm(String id, LocalDroneAcquisitionApplicationForm localDroneAcquisitionApplicationForm, MultipartFile securityClearanceDoc) throws ApplicationFormNotFoundException, UnAuthorizedAccessException, StorageException;

    LocalDroneAcquisitionApplicationForm approveLocalDroneAcquisitionForm(ApproveRequestBody approveRequestBody) throws ApplicationFormNotFoundException,UnAuthorizedAccessException;

    LocalDroneAcquisitionApplicationForm get(String id);

    Collection<?> getAcquisitionFormsOfApplicant(long applicantId);

    Collection<?> getAllAcquisitionForms();

    Resource getFile(String id, String fileName) throws StorageFileNotFoundException;

}
