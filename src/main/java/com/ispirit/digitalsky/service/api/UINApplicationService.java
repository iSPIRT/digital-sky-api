package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.UINApplication;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.exception.ApplicationNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import org.springframework.core.io.Resource;

import java.util.Collection;

public interface UINApplicationService {

    UINApplication createApplication(UINApplication uinApplication);

    UINApplication updateApplication(String id, UINApplication uinApplication) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException;

    UINApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException,UnAuthorizedAccessException;

    UINApplication get(String id);

    Collection<UINApplication> getApplicationsOfApplicant(long applicantId);

    Collection<UINApplication> getAllApplications();

    Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException;

}
