package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.ApplicationFormNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import com.ispirit.digitalsky.repository.UAOPApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.UAOPApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

public class UAOPApplicationServiceImpl implements UAOPApplicationService {


    private UAOPApplicationRepository uaopApplicationRepository;

    private StorageService storageService;

    public UAOPApplicationServiceImpl(UAOPApplicationRepository uaopApplicationRepository, StorageService storageService) {
        this.uaopApplicationRepository = uaopApplicationRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public UAOPApplication createApplication(UAOPApplication uaopApplication) {
        uaopApplication.setId(null);
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        uaopApplication.setApplicantId(userPrincipal.getId());
        UAOPApplication document = uaopApplicationRepository.insert(uaopApplication);
        storageService.store(uaopApplication.getAllDocs(), document.getId());
        return document;
    }

    @Override
    @Transactional
    public UAOPApplication updateApplication(String id, UAOPApplication uaopApplication) throws ApplicationFormNotFoundException, UnAuthorizedAccessException, StorageException {
        UAOPApplication actualForm = uaopApplicationRepository.findById(id);
        if (actualForm == null) {
            throw new ApplicationFormNotFoundException();
        }

        long applicantId = actualForm.getApplicantId();
        Date createdDate = actualForm.getCreatedDate();

        actualForm.setName(uaopApplication.getName());
        actualForm.setDesignation(uaopApplication.getDesignation());
        actualForm.setStatus(uaopApplication.getStatus());

        if (uaopApplication.getSecurityProgramDoc() != null) {
            actualForm.setSecurityProgramDocName(uaopApplication.getSecurityProgramDocName());
        }

        if (uaopApplication.getInsuranceDoc() != null) {
            actualForm.setInsuranceDocName(uaopApplication.getInsuranceDocName());
        }

        if (uaopApplication.getSopDoc() != null) {
            actualForm.setSopDocName(uaopApplication.getSopDocName());
        }

        if (uaopApplication.getLandOwnerPermissionDoc() != null) {
            actualForm.setLandOwnerPermissionDocName(uaopApplication.getLandOwnerPermissionDocName());
        }

        if (actualForm.getStatus() == ApplicationStatus.SUBMITTED) {
            actualForm.setSubmittedDate(new Date());
        }
        actualForm.setLastModifiedDate(new Date());
        actualForm.setCreatedDate(createdDate);
        actualForm.setApplicantId(applicantId);

        UAOPApplication savedForm = uaopApplicationRepository.save(actualForm);

        storageService.store(uaopApplication.getAllDocs(), savedForm.getId());

        return savedForm;
    }

    @Override
    @Transactional
    public UAOPApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationFormNotFoundException, UnAuthorizedAccessException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        UAOPApplication actualForm = uaopApplicationRepository.findById(approveRequestBody.getApplicationFormId());
        if (actualForm == null) {
            throw new ApplicationFormNotFoundException();
        }

        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());

        UAOPApplication savedForm = uaopApplicationRepository.save(actualForm);
        return savedForm;
    }

    @Override
    public UAOPApplication get(String id) {
        return uaopApplicationRepository.findById(id);

    }

    @Override
    public Collection<?> getApplicationsOfApplicant(long applicantId) {
        return uaopApplicationRepository.findByApplicant(applicantId);

    }

    @Override
    public Collection<?> getAllApplications() {
        return uaopApplicationRepository.findAll();
    }

    @Override
    public Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException {
        return storageService.loadAsResource(applicationId, fileName);
    }
}
