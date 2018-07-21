package com.ispirit.digitalsky.service;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplicationForm;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.ApplicationFormNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import com.ispirit.digitalsky.repository.EntityRepository;
import com.ispirit.digitalsky.repository.LocalDroneAcquisitionFormRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.LocalDroneAcquisitionApplicationFormService;

import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class LocalDroneAcquisitionApplicationFormServiceImpl implements LocalDroneAcquisitionApplicationFormService {

    private final LocalDroneAcquisitionFormRepository localDroneAcquisitionFormRepository;
    private final StorageService documentRepository;
    private final EntityRepository entityRepository;

    public LocalDroneAcquisitionApplicationFormServiceImpl(LocalDroneAcquisitionFormRepository localDroneAcquisitionFormRepository, StorageService documentRepository, EntityRepository entityRepository) {
        this.localDroneAcquisitionFormRepository = localDroneAcquisitionFormRepository;
        this.documentRepository = documentRepository;
        this.entityRepository = entityRepository;
    }

    @Override
    public LocalDroneAcquisitionApplicationForm createLocalDroneAcquisitionApplicationForm(LocalDroneAcquisitionApplicationForm localDroneAcquisitionApplicationForm) {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        localDroneAcquisitionApplicationForm.setApplicantId(userPrincipal.getId());
        localDroneAcquisitionApplicationForm.setCreatedDate(new Date());

        LocalDroneAcquisitionApplicationForm insertedForm = localDroneAcquisitionFormRepository.insert(localDroneAcquisitionApplicationForm);
        return insertedForm;
    }

    @Override
    public LocalDroneAcquisitionApplicationForm updateLocalDroneAcquisitionApplicationForm(String id, LocalDroneAcquisitionApplicationForm localDroneAcquisitionApplicationForm, MultipartFile securityClearanceDoc) throws ApplicationFormNotFoundException, UnAuthorizedAccessException, StorageException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        LocalDroneAcquisitionApplicationForm actualForm = localDroneAcquisitionFormRepository.findById(id);
        if (actualForm == null) {
            throw new ApplicationFormNotFoundException();
        }

        long applicantId = actualForm.getApplicantId();
        Date createdDate = actualForm.getCreatedDate();

        if (userPrincipal.getId() != applicantId) {
            throw new UnAuthorizedAccessException();
        }


        BeanUtils.copyProperties(localDroneAcquisitionApplicationForm,actualForm);
        if(actualForm.getStatus() == ApplicationStatus.SUBMITTED) {
            actualForm.setSubmittedDate(new Date());
        }
        actualForm.setLastModifiedDate(new Date());
        actualForm.setCreatedDate(createdDate);
        actualForm.setApplicantId(applicantId);

        LocalDroneAcquisitionApplicationForm savedForm = localDroneAcquisitionFormRepository.save(actualForm);

        List<MultipartFile> filesToBeUploaded = new ArrayList<MultipartFile>(Arrays.asList(securityClearanceDoc));
        documentRepository.store(filesToBeUploaded, savedForm.getId());

        return savedForm;
    }

    @Override
    public LocalDroneAcquisitionApplicationForm approveLocalDroneAcquisitionForm(ApproveRequestBody approveRequestBody) throws ApplicationFormNotFoundException, UnAuthorizedAccessException {

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        LocalDroneAcquisitionApplicationForm actualForm = localDroneAcquisitionFormRepository.findById(approveRequestBody.getApplicationFormId());
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

        LocalDroneAcquisitionApplicationForm savedForm = localDroneAcquisitionFormRepository.save(actualForm);
        return savedForm;
    }

    @Override
    public Collection<?> getAcquisitionFormsOfApplicant(long applicantId) {

        return localDroneAcquisitionFormRepository.findByApplicant(applicantId);
    }

    @Override
    public Collection<?> getAllAcquisitionForms() {

        return localDroneAcquisitionFormRepository.findAll();
    }

    @Override
    public LocalDroneAcquisitionApplicationForm get(String id) {

        return localDroneAcquisitionFormRepository.findById(id);
    }

    @Override
    public Resource getFile(String id, String fileName) throws StorageFileNotFoundException {

        return documentRepository.loadAsResource(id, fileName);
    }
}
