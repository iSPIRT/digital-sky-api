package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.DroneAcquisitionApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.DroneAcquisitionApplicationRepository;
import com.ispirit.digitalsky.repository.EntityRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.DroneAcquisitionApplicationService;

import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class DroneAcquisitionApplicationServiceImpl<T extends DroneAcquisitionApplication> implements DroneAcquisitionApplicationService<T> {

    private final DroneAcquisitionApplicationRepository<T> droneAcquisitionFormRepository;
    private final StorageService documentRepository;
    private final EntityRepository entityRepository;

    public DroneAcquisitionApplicationServiceImpl(DroneAcquisitionApplicationRepository<T> droneAcquisitionFormRepository, StorageService documentRepository, EntityRepository entityRepository) {
        this.droneAcquisitionFormRepository = droneAcquisitionFormRepository;
        this.documentRepository = documentRepository;
        this.entityRepository = entityRepository;
    }

    @Override
    @Transactional
    public T createDroneAcquisitionApplication(T droneAcquisitionApplicationForm) {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        droneAcquisitionApplicationForm.setApplicantId(userPrincipal.getId());
        droneAcquisitionApplicationForm.setCreatedDate(new Date());

        T insertedForm = droneAcquisitionFormRepository.insert(droneAcquisitionApplicationForm);
        return insertedForm;
    }

    @Override
    @Transactional
    public T updateDroneAcquisitionApplication(String id, T droneAcquisitionApplicationForm, MultipartFile securityClearanceDoc) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException, ApplicationNotEditableException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        T actualForm = droneAcquisitionFormRepository.findById(id);
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        if (!actualForm.canBeModified()) {
            throw new ApplicationNotEditableException();
        }

        long applicantId = actualForm.getApplicantId();
        Date createdDate = actualForm.getCreatedDate();

        if (userPrincipal.getId() != applicantId) {
            throw new UnAuthorizedAccessException();
        }

        BeanUtils.copyProperties(droneAcquisitionApplicationForm, actualForm);
        if(actualForm.getStatus() == ApplicationStatus.SUBMITTED) {
            actualForm.setSubmittedDate(new Date());
        }
        actualForm.setLastModifiedDate(new Date());
        actualForm.setCreatedDate(createdDate);
        actualForm.setApplicantId(applicantId);

        T savedForm = droneAcquisitionFormRepository.save(actualForm);

        List<MultipartFile> filesToBeUploaded = new ArrayList<MultipartFile>(Arrays.asList(securityClearanceDoc));
        documentRepository.store(filesToBeUploaded, savedForm.getId());

        return savedForm;
    }

    @Override
    @Transactional
    public T approveDroneAcquisitionApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        T actualForm = droneAcquisitionFormRepository.findById(approveRequestBody.getApplicationFormId());

        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());

        T savedForm = droneAcquisitionFormRepository.save(actualForm);
        return savedForm;
    }

    @Override
    public Collection<T> getApplicationsOfApplicant() {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        return droneAcquisitionFormRepository.findByApplicantId(userPrincipal.getId());
    }

    @Override
    public Collection<T> getAllApplications() {

        return droneAcquisitionFormRepository.findAll();
    }

    @Override
    public T get(String id) {

        return droneAcquisitionFormRepository.findById(id);
    }

    @Override
    public Resource getFile(String id, String fileName) throws StorageFileNotFoundException {

        return documentRepository.loadAsResource(id, fileName);
    }
}
