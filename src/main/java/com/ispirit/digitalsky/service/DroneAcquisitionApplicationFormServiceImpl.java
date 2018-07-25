package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.DroneAcquisitionApplicationForm;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.ApplicationFormNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import com.ispirit.digitalsky.repository.DroneAcquisitionFormRepository;
import com.ispirit.digitalsky.repository.EntityRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.DroneAcquisitionApplicationFormService;

import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class DroneAcquisitionApplicationFormServiceImpl<T extends DroneAcquisitionApplicationForm> implements DroneAcquisitionApplicationFormService<T> {

    private final DroneAcquisitionFormRepository<T> droneAcquisitionFormRepository;
    private final StorageService documentRepository;
    private final EntityRepository entityRepository;

    public DroneAcquisitionApplicationFormServiceImpl(DroneAcquisitionFormRepository<T> droneAcquisitionFormRepository, StorageService documentRepository, EntityRepository entityRepository) {
        this.droneAcquisitionFormRepository = droneAcquisitionFormRepository;
        this.documentRepository = documentRepository;
        this.entityRepository = entityRepository;
    }

    @Override
    public T createDroneAcquisitionApplicationForm(T droneAcquisitionApplicationForm) {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        droneAcquisitionApplicationForm.setApplicantId(userPrincipal.getId());
        droneAcquisitionApplicationForm.setCreatedDate(new Date());

        T insertedForm = droneAcquisitionFormRepository.insert(droneAcquisitionApplicationForm);
        return insertedForm;
    }

    @Override
    public T updateDroneAcquisitionApplicationForm(String id, T droneAcquisitionApplicationForm, MultipartFile securityClearanceDoc) throws ApplicationFormNotFoundException, UnAuthorizedAccessException, StorageException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        T actualForm = droneAcquisitionFormRepository.findById(id);
        if (actualForm == null) {
            throw new ApplicationFormNotFoundException();
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
    public T approveDroneAcquisitionForm(ApproveRequestBody approveRequestBody) throws ApplicationFormNotFoundException, UnAuthorizedAccessException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        T actualForm = droneAcquisitionFormRepository.findById(approveRequestBody.getApplicationFormId());

        if (actualForm == null) {
            throw new ApplicationFormNotFoundException();
        }

       //throw unauthorized if the role is not of an admin
        //if (userPrincipal.getAuthorities().forEach( );) {
        //        throw new UnAuthorizedAccessException();
       // }

        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());

        T savedForm = droneAcquisitionFormRepository.save(actualForm);
        return savedForm;
    }

    @Override
    public Collection<T> getAcquisitionFormsOfApplicant(long applicantId) {

        return droneAcquisitionFormRepository.findByApplicant(applicantId);
    }

    @Override
    public Collection<T> getAllAcquisitionForms() {

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
