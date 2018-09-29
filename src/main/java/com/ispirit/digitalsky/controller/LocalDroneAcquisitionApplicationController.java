package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;

import com.ispirit.digitalsky.service.api.DroneAcquisitionApplicationService;
import com.ispirit.digitalsky.util.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.ispirit.digitalsky.controller.LocalDroneAcquisitionApplicationController.LOCALDRONEACQUISITIONFORM_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(LOCALDRONEACQUISITIONFORM_RESOURCE_BASE_PATH)
public class LocalDroneAcquisitionApplicationController {

    public static final String LOCALDRONEACQUISITIONFORM_RESOURCE_BASE_PATH = "/api/applicationForm/localDroneAcquisitionApplication";

    private DroneAcquisitionApplicationService<LocalDroneAcquisitionApplication> droneAcquisitionFormService;
    private CustomValidator validator;

    @Autowired
    public LocalDroneAcquisitionApplicationController(DroneAcquisitionApplicationService<LocalDroneAcquisitionApplication> droneAcquisitionFormService, CustomValidator validator) {
        this.droneAcquisitionFormService = droneAcquisitionFormService;
        this.validator = validator;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAcquisitionForm(@RequestBody LocalDroneAcquisitionApplication acquisitionForm) {

        try {
            LocalDroneAcquisitionApplication createdForm = droneAcquisitionFormService.createDroneAcquisitionApplication(acquisitionForm);
            return new ResponseEntity<>(createdForm, HttpStatus.CREATED);
        } catch(Exception e){
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAcquisitionForm(@PathVariable String id, @RequestParam(value="securityClearanceDoc", required = false) MultipartFile securityClearanceDoc, @RequestParam(value="droneAcquisitionForm") String droneAcquisitionFormString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            LocalDroneAcquisitionApplication droneAcquisitionForm = mapper.readValue(droneAcquisitionFormString, LocalDroneAcquisitionApplication.class);
            if(droneAcquisitionForm.isSubmitted()){
                validator.validate(droneAcquisitionForm);
            }
            LocalDroneAcquisitionApplication updatedForm = droneAcquisitionFormService.updateDroneAcquisitionApplication(id, droneAcquisitionForm, securityClearanceDoc);
            return new ResponseEntity<>(updatedForm, HttpStatus.OK);
        } catch (JsonGenerationException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (JsonMappingException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ApplicationNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch(ApplicationNotEditableException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (StorageException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @RequestMapping(value = "/approve/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveAcquisitionForm(@PathVariable String id, @Valid @RequestBody ApproveRequestBody approveRequestBody) {

        try {
            LocalDroneAcquisitionApplication updatedForm = droneAcquisitionFormService.approveDroneAcquisitionApplication(approveRequestBody);
            return new ResponseEntity<>(updatedForm, HttpStatus.OK);
        } catch (ApplicationNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch(ValidationException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listApplications() {

        Collection<?> applicationForms = droneAcquisitionFormService.getApplicationsOfApplicant();
        return new ResponseEntity<>(applicationForms,HttpStatus.OK);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listAll() {

        Collection<?> applicationForms = droneAcquisitionFormService.getAllApplications();
        List<?> submittedApplications = applicationForms.stream().filter(applicationForm -> {
            ApplicationStatus status = ((LocalDroneAcquisitionApplication) applicationForm).getStatus();
            return status != null && status != ApplicationStatus.DRAFT;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(submittedApplications, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAcquisitionForm(@PathVariable String id){
        try {
            LocalDroneAcquisitionApplication applicationForm = droneAcquisitionFormService.get(id);
            return new ResponseEntity<>(applicationForm, HttpStatus.OK);
        } catch(UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch(Exception e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
    }


    @RequestMapping(value = "/{id}/document/{documentName:.+}", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@PathVariable String id, @PathVariable String documentName){
        try {
            Resource resourceFile = droneAcquisitionFormService.getFile(id, documentName);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resourceFile.getFilename() + "\"").body(resourceFile);
        } catch(StorageFileNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch(UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch(Exception e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
    }
}

