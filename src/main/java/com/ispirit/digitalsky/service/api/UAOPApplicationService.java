package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.exception.ApplicationNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import org.springframework.core.io.Resource;

import java.util.Collection;

public interface UAOPApplicationService {

    UAOPApplication createApplication(UAOPApplication  uaopApplication);

    UAOPApplication updateApplication(String id, UAOPApplication uaopApplication) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException;

    UAOPApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException,UnAuthorizedAccessException;

    UAOPApplication get(String id);

    Collection<?> getApplicationsOfApplicant(long applicantId);

    Collection<?> getAllApplications();

    Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException;

}
