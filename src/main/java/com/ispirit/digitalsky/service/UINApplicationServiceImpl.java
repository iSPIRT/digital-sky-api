package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.UINApplication;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.UINApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UINApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

public class UINApplicationServiceImpl implements UINApplicationService {


    private UINApplicationRepository uinApplicationRepository;
    private OperatorDroneService operatorDroneService;

    private StorageService storageService;

    public UINApplicationServiceImpl(UINApplicationRepository uinApplicationRepository, StorageService storageService, OperatorDroneService operatorDroneService ) {

        this.uinApplicationRepository = uinApplicationRepository;
        this.storageService = storageService;
        this.operatorDroneService = operatorDroneService;
    }

    @Override
    @Transactional
    public UINApplication createApplication(UINApplication uinApplication) {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        uinApplication.setApplicantId(userPrincipal.getId());
        uinApplication.setApplicant(userPrincipal.getUsername());
        UINApplication document = uinApplicationRepository.insert(uinApplication);
        storageService.store(uinApplication.getAllDocs(), document.getId());

        operatorDroneService.updateUINApplicationId(uinApplication.getOperatorDroneId(), uinApplication.getId(), OperatorDroneStatus.UIN_DRAFT);

        return document;
    }

    @Override
    @Transactional
    public UINApplication updateApplication(String id, UINApplication uinApplication) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException {

        UINApplication actualForm = uinApplicationRepository.findById(id);
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        long applicantId = actualForm.getApplicantId();
        Date createdDate = actualForm.getCreatedDate();

        if (uinApplication.getImportPermissionDoc() != null) {
            actualForm.setImportPermissionDocName(uinApplication.getImportPermissionDocName());
        }

        if (uinApplication.getCinDoc() != null) {
            actualForm.setCinDocName(uinApplication.getCinDocName());
        }

        if (uinApplication.getGstinDoc() != null) {
            actualForm.setGstinDocName(uinApplication.getGstinDocName());
        }

        if (uinApplication.getPanCardDoc() != null) {
            actualForm.setPanCardDocName(uinApplication.getPanCardDocName());
        }

        if (uinApplication.getSecurityClearanceDoc() != null) {
            actualForm.setSecurityClearanceDocName(uinApplication.getSecurityClearanceDocName());
        }

        if (uinApplication.getDotPermissionDoc() != null) {
            actualForm.setDotPermissionDocName(uinApplication.getDotPermissionDocName());
        }

        if (uinApplication.getEtaDoc() != null) {
            actualForm.setEtaDocName(uinApplication.getEtaDocName());
        }

        if (uinApplication.getOpManualDoc() != null) {
            actualForm.setOpManualDocName(uinApplication.getOpManualDocName());
        }

        if (uinApplication.getMaintenanceGuidelinesDoc() != null) {
            actualForm.setMaintenanceGuidelinesDocName(uinApplication.getMaintenanceGuidelinesDocName());
        }

        BeanUtils.copyProperties(uinApplication, actualForm);

        if (actualForm.getStatus() == ApplicationStatus.SUBMITTED) {
            actualForm.setSubmittedDate(new Date());
            operatorDroneService.updateStatus(uinApplication.getOperatorDroneId(), OperatorDroneStatus.UIN_SUBMITTED);
        }

        if(actualForm.getUniqueDeviceId() !=null) {
            operatorDroneService.updateUniqueDeviceId(uinApplication.getOperatorDroneId(), actualForm.getUniqueDeviceId());
        }

        actualForm.setLastModifiedDate(new Date());
        actualForm.setCreatedDate(createdDate);
        actualForm.setApplicantId(applicantId);

        UINApplication savedForm = uinApplicationRepository.save(actualForm);

        storageService.store(uinApplication.getAllDocs(), savedForm.getId());

        return savedForm;
    }

    @Override
    @Transactional
    public UINApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        UINApplication actualForm = uinApplicationRepository.findById(approveRequestBody.getApplicationFormId());
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

        OperatorDroneStatus opdroneStatus = approveRequestBody.getStatus() == ApplicationStatus.APPROVED ? OperatorDroneStatus.UIN_APPROVED : OperatorDroneStatus.UIN_REJECTED;
        operatorDroneService.updateStatus(actualForm.getOperatorDroneId(), opdroneStatus);

        if(opdroneStatus == OperatorDroneStatus.UIN_REJECTED) {
            operatorDroneService.updateUniqueDeviceId(actualForm.getOperatorDroneId(), null);
        }

        UINApplication savedForm = uinApplicationRepository.save(actualForm);
        return savedForm;
    }

    @Override
    public UINApplication get(String id) {
        return uinApplicationRepository.findById(id);

    }

    @Override
    public Collection<UINApplication> getApplicationsOfApplicant(long applicantId) {
        return uinApplicationRepository.findByApplicantId(applicantId);

    }

    @Override
    public Collection<UINApplication> getAllApplications() {
        return uinApplicationRepository.findAll();
    }

    @Override
    public Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException {
        return storageService.loadAsResource(applicationId, fileName);
    }
}
