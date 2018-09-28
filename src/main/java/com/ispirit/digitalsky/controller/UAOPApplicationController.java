package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.service.api.UAOPApplicationService;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.ispirit.digitalsky.controller.UAOPApplicationController.UAOP_APPLICATION_RESOURCE_BASE_PATH;
import static com.ispirit.digitalsky.util.FileStoreHelper.resolveFileName;

@RestController
@RequestMapping(UAOP_APPLICATION_RESOURCE_BASE_PATH)
public class UAOPApplicationController {

    public static final String UAOP_APPLICATION_RESOURCE_BASE_PATH = "/api/applicationForm/uaopApplication";

    private UAOPApplicationService uaopApplicationService;
    private CustomValidator validator;

    @Autowired
    public UAOPApplicationController(UAOPApplicationService uaopApplicationService, CustomValidator validator) {

        this.uaopApplicationService = uaopApplicationService;
        this.validator = validator;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createApplication(
            @RequestParam(value = "securityProgramDoc", required = false) MultipartFile securityProgramDoc,
            @RequestParam(value = "sopDoc", required = false) MultipartFile sopDoc,
            @RequestParam(value = "insuranceDoc", required = false) MultipartFile insuranceDoc,
            @RequestParam(value = "landOwnerPermissionDoc", required = false) MultipartFile landOwnerPermissionDoc,
            @RequestParam(value = "uaopApplicationForm") String uaopApplicationFormString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            UAOPApplication uaopApplication = mapper.readValue(uaopApplicationFormString, UAOPApplication.class);
            appendDocs(uaopApplication, securityProgramDoc, sopDoc, insuranceDoc, landOwnerPermissionDoc);
            if(uaopApplication.isSubmitted()){
                validator.validate(uaopApplication);
            }
            UAOPApplication createdForm = uaopApplicationService.createApplication(uaopApplication);
            return new ResponseEntity<>(createdForm, HttpStatus.CREATED);
        } catch (ValidationException e) {
            throw  e;
        } catch (Exception e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);
        }
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateApplication(
            @PathVariable String id,
            @RequestParam(value = "securityProgramDoc", required = false) MultipartFile securityProgramDoc,
            @RequestParam(value = "sopDoc", required = false) MultipartFile sopDoc,
            @RequestParam(value = "insuranceDoc", required = false) MultipartFile insuranceDoc,
            @RequestParam(value = "landOwnerPermissionDoc", required = false) MultipartFile landOwnerPermissionDoc,
            @RequestParam(value = "uaopApplicationForm") String uaopApplicationFormString) {

        try {
            UserPrincipal userPrincipal = UserPrincipal.securityContext();
            UAOPApplication application = uaopApplicationService.get(id);

            if(application == null) throw new EntityNotFoundException("UAOPApplication", id);

            if (userPrincipal.getId() != application.getApplicantId()) {
                return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
            }

            if (!application.canBeModified()) {
                return new ResponseEntity<>(new Errors("Application not in draft status, cannot be modified"), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            ObjectMapper mapper = new ObjectMapper();
            UAOPApplication uaopApplication = mapper.readValue(uaopApplicationFormString, UAOPApplication.class);
            appendDocs(uaopApplication, securityProgramDoc, sopDoc, insuranceDoc, landOwnerPermissionDoc);
            if(uaopApplication.isSubmitted()){
                validator.validate(uaopApplication);
            }
            UAOPApplication createdForm = uaopApplicationService.updateApplication(id, uaopApplication);
            return new ResponseEntity<>(createdForm, HttpStatus.OK);
        } catch (EntityNotFoundException | ValidationException e) {
            throw  e;
        } catch (Exception e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);
        }
    }


    @RequestMapping(value = "/approve/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveApplication(@PathVariable String id, @Valid @RequestBody ApproveRequestBody approveRequestBody) {
        try {
            UAOPApplication updatedForm = uaopApplicationService.approveApplication(approveRequestBody);
            return new ResponseEntity<>(updatedForm, HttpStatus.OK);
        } catch (ApplicationNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }catch (ApplicationNotInSubmittedStatusException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listApplications() {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        Collection<?> applicationForms = uaopApplicationService.getApplicationsOfApplicant(userPrincipal.getId());
        return new ResponseEntity<>(applicationForms, HttpStatus.OK);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listAll() {

        Collection<?> applicationForms = uaopApplicationService.getAllApplications();
        List<?> submittedApplications = applicationForms.stream().filter(applicationForm -> {
            ApplicationStatus status = ((UAOPApplication) applicationForm).getStatus();
            return status != null && status != ApplicationStatus.DRAFT;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(submittedApplications, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getApplication(@PathVariable String id) {

        UAOPApplication applicationForm = uaopApplicationService.get(id);

        if(applicationForm == null){
            return new ResponseEntity<>(new Errors("Application Not Found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != applicationForm.getApplicantId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(applicationForm, HttpStatus.OK);
    }

    @RequestMapping(value = "/{applicationId}/document/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@PathVariable String applicationId, @PathVariable String fileName) {

        try {
            UAOPApplication applicationForm = uaopApplicationService.get(applicationId);

            if(applicationForm == null){
                return new ResponseEntity<>(new Errors("Application Not Found"), HttpStatus.BAD_REQUEST);
            }

            UserPrincipal userPrincipal = UserPrincipal.securityContext();
            if (!userPrincipal.isAdmin() && userPrincipal.getId() != applicationForm.getApplicantId()) {
                return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
            }
            Resource resourceFile = uaopApplicationService.getFile(applicationId, fileName);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resourceFile.getFilename() + "\"").body(resourceFile);
        } catch (StorageFileNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    private void appendDocs(UAOPApplication uaopApplication, MultipartFile securityProgramDoc, MultipartFile sopDoc, MultipartFile insuranceDoc, MultipartFile landOwnerPermissionDoc) {
        uaopApplication.setInsuranceDoc(insuranceDoc);
        uaopApplication.setInsuranceDocName(resolveFileName(insuranceDoc));

        uaopApplication.setLandOwnerPermissionDoc(landOwnerPermissionDoc);
        uaopApplication.setLandOwnerPermissionDocName(resolveFileName(landOwnerPermissionDoc));

        uaopApplication.setSopDoc(sopDoc);
        uaopApplication.setSopDocName(resolveFileName(sopDoc));

        uaopApplication.setSecurityProgramDoc(securityProgramDoc);
        uaopApplication.setSecurityProgramDocName(resolveFileName(securityProgramDoc));
    }

}

