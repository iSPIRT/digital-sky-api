package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.LocalDroneAcquisitionApplicationForm;
import com.ispirit.digitalsky.repository.EntityRepository;
import com.ispirit.digitalsky.repository.LocalDroneAcquisitionFormRepository;
import com.ispirit.digitalsky.repository.storage.StorageFileNotFoundException;
import com.ispirit.digitalsky.repository.storage.StorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/applicationForm/localDroneAcquisition")
public class LocalDroneAcquisitionFormRestController {

    private final LocalDroneAcquisitionFormRepository localDroneAcquisitionFormRepository;
    private final StorageService documentRepository;
    private final EntityRepository entityRepository;

    @Autowired
    public LocalDroneAcquisitionFormRestController(LocalDroneAcquisitionFormRepository localDroneAcquisitionFormRepository, StorageService documentRepository, EntityRepository entityRepository) {
        this.localDroneAcquisitionFormRepository = localDroneAcquisitionFormRepository;
        this.documentRepository = documentRepository;
        this.entityRepository = entityRepository;
    }

    @PostMapping
    public LocalDroneAcquisitionApplicationForm saveAcquisitionForm(@RequestPart LocalDroneAcquisitionApplicationForm acquisitionForm, @RequestPart(value="securityClearanceDoc", required=false) MultipartFile securityClearanceDoc, @RequestPart(value="etaClearanceDoc", required=false) MultipartFile etaClearanceDoc) {

        List<MultipartFile> filesToBeUploaded = new ArrayList<MultipartFile>(Arrays.asList(securityClearanceDoc, etaClearanceDoc));
        acquisitionForm.setLastModifiedDate(new Date());
        acquisitionForm.setSubmittedDate(new Date());
        LocalDroneAcquisitionApplicationForm insertedForm = localDroneAcquisitionFormRepository.insert(acquisitionForm);
        documentRepository.store(filesToBeUploaded, insertedForm.getId());
        entityRepository.createAcquisitionForm();
        return insertedForm;
    }

    @PatchMapping("/{applicationFormId}")
    public LocalDroneAcquisitionApplicationForm editAcquisitionForm(@PathVariable String applicationFormId, @RequestBody LocalDroneAcquisitionApplicationForm acquisitionForm) {
        LocalDroneAcquisitionApplicationForm actualForm = localDroneAcquisitionFormRepository.findById(applicationFormId);
        BeanUtils.copyProperties(acquisitionForm,actualForm);
        actualForm.setLastModifiedDate(new Date());
        LocalDroneAcquisitionApplicationForm savedForm = localDroneAcquisitionFormRepository.save(actualForm);
        return savedForm;
    }

    @PatchMapping("/approve/{applicationFormId}")
    public LocalDroneAcquisitionApplicationForm approveForm(@PathVariable String applicationFormId, @RequestBody ApproveRequestBody approveRequestBody) {
        LocalDroneAcquisitionApplicationForm actualForm = localDroneAcquisitionFormRepository.findById(applicationFormId);
        try {
           actualForm.setApprovedById(approveRequestBody.getApprovedById());
           actualForm.setStatus(approveRequestBody.getStatus());
           actualForm.setApprovedDate(approveRequestBody.getApprovedDate());
           actualForm.setApproverComments(approveRequestBody.getComments());
           LocalDroneAcquisitionApplicationForm savedForm = localDroneAcquisitionFormRepository.save(actualForm);
           return savedForm;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return actualForm;
    }

    @GetMapping("/getByApplicantId/{applicantName}")
    public Collection<LocalDroneAcquisitionApplicationForm> getAcquisitionForms(@PathVariable String applicantName){
        return localDroneAcquisitionFormRepository.findByApplicant(applicantName);
    }

    @GetMapping("/getAll")
    public Collection<LocalDroneAcquisitionApplicationForm> getAcquisitionForms(){
        return localDroneAcquisitionFormRepository.findAll();
    }

    @GetMapping("/{applicationId}")
    public LocalDroneAcquisitionApplicationForm getAcquisitionForm(@PathVariable String applicationId){
        return localDroneAcquisitionFormRepository.findById(applicationId);
    }

    @GetMapping(value = "/files/{applicationFormId}/{fileName}", produces = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String applicationFormId, @PathVariable String fileName){
        Resource resourceFile = documentRepository.loadAsResource(applicationFormId, fileName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resourceFile.getFilename() + "\"").body(resourceFile);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}

