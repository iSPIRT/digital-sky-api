package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ApplicationNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import com.ispirit.digitalsky.exception.*;
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
        uaopApplication.setApplicant(userPrincipal.getUsername());
        if (uaopApplication.getStatus() == ApplicationStatus.SUBMITTED) {
            uaopApplication.setSubmittedDate(new Date());
        }
        UAOPApplication document = uaopApplicationRepository.insert(uaopApplication);
        storageService.store(uaopApplication.getAllDocs(), document.getId());
        return document;
    }

    @Override
    @Transactional
    public UAOPApplication updateApplication(String id, UAOPApplication uaopApplication) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException, ValidationException {
        UAOPApplication actualForm = uaopApplicationRepository.findById(id);
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        if(uaopApplication.getStatus() == ApplicationStatus.SUBMITTED &&
            (
                    (uaopApplication.getSecurityProgramDocName()==null && actualForm.getSecurityProgramDocName()==null) ||
                    (uaopApplication.getInsuranceDocName()==null && actualForm.getInsuranceDocName()==null) ||
                    (uaopApplication.getSopDocName()==null && actualForm.getSopDocName()==null) ||
                    (uaopApplication.getLandOwnerPermissionDocName()==null && actualForm.getLandOwnerPermissionDocName()==null) ||
                    (uaopApplication.getPaymentReceiptDocName()==null && actualForm.getPaymentReceiptDocName()==null)
            )){
            throw new ValidationException(new Errors("All required files not provided"));
        }


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

        if (uaopApplication.getPaymentReceiptDoc() != null) {
            actualForm.setPaymentReceiptDocName(uaopApplication.getPaymentReceiptDocName());
        }

        if (uaopApplication.getStatus() == ApplicationStatus.SUBMITTED) {
            actualForm.setSubmittedDate(new Date());
        }
        actualForm.setLastModifiedDate(new Date());

        UAOPApplication savedForm = uaopApplicationRepository.save(actualForm);

        storageService.store(uaopApplication.getAllDocs(), savedForm.getId());

        return savedForm;
    }

    @Override
    @Transactional
    public UAOPApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        UAOPApplication actualForm = uaopApplicationRepository.findById(approveRequestBody.getApplicationFormId());
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        if (actualForm.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new ApplicationNotInSubmittedStatusException();
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
    public Collection<UAOPApplication> getApplicationsOfApplicant(long applicantId) {
        return uaopApplicationRepository.findByApplicantId(applicantId);

    }

    @Override
    public Collection<UAOPApplication> getAllApplications() {
        return uaopApplicationRepository.findAll();
    }

    @Override
    public Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException {
        return storageService.loadAsResource(applicationId, fileName);
    }
}
