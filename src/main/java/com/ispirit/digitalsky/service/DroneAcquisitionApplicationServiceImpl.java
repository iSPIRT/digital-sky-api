package com.ispirit.digitalsky.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.DroneAcquisitionApplication;
import com.ispirit.digitalsky.document.ImportDroneApplication;

import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;

import com.ispirit.digitalsky.repository.DroneAcquisitionApplicationRepository;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;


import com.ispirit.digitalsky.service.api.*;

import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.ispirit.digitalsky.util.FileStoreHelper.resolveFileName;

public class DroneAcquisitionApplicationServiceImpl<T extends DroneAcquisitionApplication> implements DroneAcquisitionApplicationService<T> {

    private final DroneAcquisitionApplicationRepository<T> droneAcquisitionFormRepository;

    private final StorageService storageService;
    private final OperatorDroneService operatorDroneService;
    private final DroneTypeService droneService;
    private final UserProfileService userProfileService;

    public DroneAcquisitionApplicationServiceImpl(DroneAcquisitionApplicationRepository<T> droneAcquisitionFormRepository,
                                                  StorageService storageService,
                                                  DroneTypeService droneService,
                                                  OperatorDroneService operatorDroneService,
                                                  UserProfileService userProfileService) {
        this.droneAcquisitionFormRepository = droneAcquisitionFormRepository;
        this.storageService = storageService;
        this.droneService = droneService;
        this.operatorDroneService = operatorDroneService;
        this.userProfileService = userProfileService;
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

        if (securityClearanceDoc != null) {
            String docName = resolveFileName(securityClearanceDoc);
            actualForm.setSecurityClearanceDocName(docName);
        }

        T savedForm = droneAcquisitionFormRepository.save(actualForm);

        List<MultipartFile> filesToBeUploaded = new ArrayList<MultipartFile>(Arrays.asList(securityClearanceDoc));
        storageService.store(filesToBeUploaded, savedForm.getId());

        return savedForm;
    }

    @Override
    @Transactional
    public T approveDroneAcquisitionApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException, IOException {

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
        if(savedForm.getStatus() == ApplicationStatus.APPROVED) {

            boolean isImported = actualForm instanceof ImportDroneApplication;

            long operatorId;

            ApplicantType operatorType;

            UserProfile userProfile = userProfileService.profile(actualForm.getApplicantId());

            if (userProfile.isIndividualOperator()) {
                operatorId = userProfile.getIndividualOperatorId();
                operatorType = ApplicantType.INDIVIDUAL;
            } else if (userProfile.isOrganizationOperator()) {
                operatorId = userProfile.getOrgOperatorId();
                operatorType = ApplicantType.ORGANISATION;
            } else {
                throw new ValidationException(new Errors("Applicant not operator"));
            }

            DroneType actualDroneType = droneService.get(actualForm.getDroneTypeId());

            OperatorDrone opDrone = new OperatorDrone(operatorId, operatorType, actualForm.getId(), isImported);
            List<OperatorDrone> operatorDrones = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for (int i = 0; i < savedForm.getNoOfDrones(); i++) {

                OperatorDrone deepCopy = objectMapper.readValue(objectMapper.writeValueAsString(opDrone), OperatorDrone.class);
                deepCopy.setDroneType(actualDroneType);
                operatorDrones.add(deepCopy);
            }

            operatorDroneService.createOperatorDrones(operatorDrones);
            }

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
    public Resource getFile(String id, String fileName) throws StorageFileNotFoundException, UnAuthorizedAccessException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        T applicationForm = get(id);
        if (!userPrincipal.isAdmin() && userPrincipal.getId() != applicationForm.getApplicantId())
            throw new UnAuthorizedAccessException();

        return storageService.loadAsResource(id, fileName);
    }
}
