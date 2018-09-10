package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.FlyDronePermissionApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.FlyDronePermissionApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FlyDronePermissionApplicationServiceImpl implements FlyDronePermissionApplicationService {


    private FlyDronePermissionApplicationRepository repository;

    private StorageService storageService;

    private  UserPrincipal userPrincipal;


    public FlyDronePermissionApplicationServiceImpl(FlyDronePermissionApplicationRepository repository, StorageService storageService) {
        this.repository = repository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication createApplication(FlyDronePermissionApplication application) {
        application.setId(null);
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        application.setApplicantId(userPrincipal.getId());
        application.setApplicant(userPrincipal.getUsername());
        FlyDronePermissionApplication document = repository.insert(application);
        if (application.getStatus() == ApplicationStatus.SUBMITTED) {
            application.setSubmittedDate(new Date());
        }
        return document;
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication updateApplication(String id, FlyDronePermissionApplication application) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException {
        FlyDronePermissionApplication actualForm = repository.findById(id);
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        actualForm.setPilotId(application.getPilotId());
        actualForm.setFlyArea(application.getFlyArea());
        actualForm.setStatus(application.getStatus());

        if (application.getStatus() == ApplicationStatus.SUBMITTED) {
            actualForm.setSubmittedDate(new Date());
        }
        actualForm.setLastModifiedDate(new Date());

        FlyDronePermissionApplication savedForm = repository.save(actualForm);

        return savedForm;
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        FlyDronePermissionApplication actualForm = repository.findById(approveRequestBody.getApplicationFormId());
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        if (actualForm.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new ApplicationNotInSubmittedStatus();
        }

        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());

        FlyDronePermissionApplication savedForm = repository.save(actualForm);
        return savedForm;
    }

    @Override
    public FlyDronePermissionApplication get(String id) {
        return repository.findById(id);

    }

    @Override
    public Collection<FlyDronePermissionApplication> getApplicationsOfDrone(long droneId) {
        Collection<FlyDronePermissionApplication> applications = repository.findByDroneId(droneId);
        return applications.stream().sorted((o1, o2) -> o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate())).collect(Collectors.toList());

    }

    @Override
    public Collection<FlyDronePermissionApplication> getAllApplications() {
        List<FlyDronePermissionApplication> applications = repository.findAll();
        return applications.stream().sorted((o1, o2) -> o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate())).collect(Collectors.toList());
    }

    @Override
    public Resource getFile(String applicationId, String fileName) throws StorageFileNotFoundException {
        return storageService.loadAsResource(applicationId, fileName);
    }

}
