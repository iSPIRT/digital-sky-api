package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.UINApplication;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.DroneDeviceRepository;
import com.ispirit.digitalsky.repository.UINApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UINApplicationService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

public class UINApplicationServiceImpl implements UINApplicationService {


    private final UINApplicationRepository uinApplicationRepository;
    private final OperatorDroneService operatorDroneService;
    private final UserProfileService userProfileService;
    private final DroneDeviceRepository droneDeviceRepository;

    private final StorageService storageService;

    public UINApplicationServiceImpl(UINApplicationRepository uinApplicationRepository,
                                     StorageService storageService,
                                     OperatorDroneService operatorDroneService,
                                     UserProfileService userProfileService,
                                     DroneDeviceRepository droneDeviceRepository
                                     ) {

        this.uinApplicationRepository = uinApplicationRepository;
        this.userProfileService = userProfileService;
        this.storageService = storageService;
        this.operatorDroneService = operatorDroneService;
        this.droneDeviceRepository = droneDeviceRepository;
    }

    @Override
    @Transactional
    public UINApplication createApplication(UINApplication uinApplication) throws OperatorNotAuthorizedException, ValidationException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if(isAuthorizedOperatorDrone(uinApplication.getOperatorDroneId())) {
            uinApplication.setApplicantId(userPrincipal.getId());
            setOperatorDetails(userPrincipal, uinApplication);
            uinApplication.setApplicant(userPrincipal.getUsername());
            uinApplication.setApplicantEmail(userPrincipal.getEmail());
            UINApplication document = uinApplicationRepository.insert(uinApplication);
            storageService.store(uinApplication.getAllDocs(), document.getId());
            operatorDroneService.updateUINApplicationId(uinApplication.getOperatorDroneId(), uinApplication.getId(), OperatorDroneStatus.UIN_DRAFT);
            return document;
        } else {
            throw new OperatorNotAuthorizedException();
        }
    }

    @Override
    @Transactional
    public UINApplication updateApplication(String id, UINApplication uinApplication) throws OperatorNotAuthorizedException, StorageException, ValidationException, DeviceUniqueIdMissingException, DeviceAlreadyUsedInAnotherUINApplicationException {

        UINApplication actualForm = uinApplicationRepository.findById(id);

        if(!isAuthorizedOperatorDrone(uinApplication.getOperatorDroneId())) {
            throw new OperatorNotAuthorizedException();
        }

        if (isValidDroneDevice(uinApplication)) {
            long applicantId = actualForm.getApplicantId();
            Date createdDate = actualForm.getCreatedDate();
            UserPrincipal userPrincipal = UserPrincipal.securityContext();

            setDocumentNames(uinApplication, actualForm);
            BeanUtils.copyProperties(uinApplication, actualForm);
            if (actualForm.getStatus() == ApplicationStatus.SUBMITTED) {
                actualForm.setSubmittedDate(new Date());
                operatorDroneService.updateStatus(uinApplication.getOperatorDroneId(), OperatorDroneStatus.UIN_SUBMITTED);
            }
            operatorDroneService.updateUniqueDeviceId(uinApplication.getOperatorDroneId(), uinApplication.getUniqueDeviceId());

            actualForm.setLastModifiedDate(new Date());
            actualForm.setCreatedDate(createdDate);
            actualForm.setApplicantId(applicantId);
            setOperatorDetails(userPrincipal, actualForm);
            UINApplication savedForm = uinApplicationRepository.save(actualForm);
            storageService.store(uinApplication.getAllDocs(), savedForm.getId());

            return savedForm;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public UINApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, ApplicationNotInSubmittedStatusException {
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

        if(approveRequestBody.getStatus() == ApplicationStatus.APPROVED)
            operatorDroneService.createUinNumberForDevice(actualForm.getOperatorDroneId());
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

    private boolean isValidDroneDevice(UINApplication uinApplication) throws DeviceUniqueIdMissingException, OperatorNotAuthorizedException, DeviceAlreadyUsedInAnotherUINApplicationException, ValidationException {

        if(uinApplication.getStatus() == ApplicationStatus.SUBMITTED && uinApplication.getUniqueDeviceId() == null ) {
            throw new DeviceUniqueIdMissingException();
        }

        if(uinApplication.getUniqueDeviceId() != null) {

            UserPrincipal userPrincipal = UserPrincipal.securityContext();
            long userId = userPrincipal.getId();
            long operatorId;
            ApplicantType operatorType;
            UserProfile userProfile = userProfileService.profile(userId);

            if (userProfile.isIndividualOperator()) {
                operatorId = userProfile.getIndividualOperatorId();
                operatorType = ApplicantType.INDIVIDUAL;
            } else if (userProfile.isOrganizationOperator()) {
                operatorId = userProfile.getOrgOperatorId();
                operatorType = ApplicantType.ORGANISATION;
            } else {
                throw new ValidationException(new Errors("Applicant not operator"));
            }

            DroneDevice device = droneDeviceRepository.findByDeviceId(uinApplication.getUniqueDeviceId());

            if (!device.getOperatorBusinessIdentifier().equals(String.valueOf(userProfile.getOperatorBusinessIdentifier()))) {
                throw new OperatorNotAuthorizedException();
            }

            if (operatorDroneService.isMappedToDifferentUIN(uinApplication.getUniqueDeviceId(), uinApplication.getId(), operatorId, operatorType)) {
                throw new DeviceAlreadyUsedInAnotherUINApplicationException();
            }
        }
        return true;
    }

    private void setDocumentNames(UINApplication uinApplication, UINApplication actualForm) {

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
    }

    private boolean isAuthorizedOperatorDrone(long operatorDroneId) throws ValidationException{
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        long userId = userPrincipal.getId();
        long operatorId ;
        ApplicantType operatorType;
        UserProfile userProfile = userProfileService.profile(userId);

        if (userProfile.isIndividualOperator()) {
            operatorId = userProfile.getIndividualOperatorId();
            operatorType = ApplicantType.INDIVIDUAL;
        } else if (userProfile.isOrganizationOperator()) {
            operatorId = userProfile.getOrgOperatorId();
            operatorType = ApplicantType.ORGANISATION;
        } else {
            throw new ValidationException(new Errors("Applicant not operator"));
        }
        OperatorDrone operatorDrone= operatorDroneService.find(operatorDroneId);
        boolean isAuthorized = (operatorDrone.getOperatorId() == operatorId  && operatorDrone.getOperatorType() == operatorType);
        return isAuthorized;
    }

    private void setOperatorDetails(UserPrincipal userPrincipal, UINApplication uinApplication) {
        UserProfile userProfile = userProfileService.profile(userPrincipal.getId());
        long operatorId;
        ApplicantType operatorType;

        if (userProfile.isIndividualOperator()) {
            operatorId = userProfile.getIndividualOperatorId();
            operatorType = ApplicantType.INDIVIDUAL;
        } else if (userProfile.isOrganizationOperator()) {
            operatorId = userProfile.getOrgOperatorId();
            operatorType = ApplicantType.ORGANISATION;
        } else {
            throw new ValidationException(new Errors("Applicant not operator"));
        }

        uinApplication.setOperatorId(operatorId);
        uinApplication.setApplicantType(operatorType);
    }


}
