package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.ImportedDroneAcquisitionApplication;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.service.api.DroneAcquisitionApplicationService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

import static com.ispirit.digitalsky.controller.ImportDroneApplicationRestController.IMPORTEDDRONEACQUISITIONFORM_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(IMPORTEDDRONEACQUISITIONFORM_RESOURCE_BASE_PATH)
public class ImportDroneApplicationRestController {

    public static final String IMPORTEDDRONEACQUISITIONFORM_RESOURCE_BASE_PATH = "/api/applicationForm/importDroneApplication";

    private DroneAcquisitionApplicationService<ImportedDroneAcquisitionApplication> droneAcquisitionFormService;

    public ImportDroneApplicationRestController(DroneAcquisitionApplicationService<ImportedDroneAcquisitionApplication> droneAcquisitionFormService) {

        this.droneAcquisitionFormService = droneAcquisitionFormService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAcquisitionForm(@RequestBody ImportedDroneAcquisitionApplication acquisitionForm) {

        try {
            ImportedDroneAcquisitionApplication createdForm = droneAcquisitionFormService.createDroneAcquisitionApplicationForm(acquisitionForm);
            return new ResponseEntity<>(createdForm, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAcquisitionForm(@PathVariable String id, @RequestParam(value="securityClearanceDocument", required = false) MultipartFile securityClearanceDoc, @RequestParam(value="droneAcquisitionForm") String droneAcquisitionFormString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            ImportedDroneAcquisitionApplication droneAcquisitionForm = mapper.readValue(droneAcquisitionFormString, ImportedDroneAcquisitionApplication.class);
            ImportedDroneAcquisitionApplication updatedForm = droneAcquisitionFormService.updateDroneAcquisitionApplicationForm(id, droneAcquisitionForm, securityClearanceDoc);
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
    public ResponseEntity<?> approveAcquisitionForm(@PathVariable String id, @RequestBody ApproveRequestBody approveRequestBody) {

        try {
            ImportedDroneAcquisitionApplication updatedForm = droneAcquisitionFormService.approveDroneAcquisitionForm(approveRequestBody);
            return new ResponseEntity<>(updatedForm, HttpStatus.OK);
        } catch (ApplicationNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/getByApplicant/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAcquisitionFormsOfApplicant(@PathVariable long id){

        Collection<?> applicationForms = droneAcquisitionFormService.getAcquisitionFormsOfApplicant(id);
        return new ResponseEntity<>(applicationForms,HttpStatus.OK);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAcquisitionForms(){

        Collection<?> applicationForms = droneAcquisitionFormService.getAllAcquisitionForms();
        return new ResponseEntity<>(applicationForms,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAcquisitionForm(@PathVariable String id){

        ImportedDroneAcquisitionApplication applicationForm = droneAcquisitionFormService.get(id);
        return new ResponseEntity<>(applicationForm,HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/document/{documentName}", method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getFile(@PathVariable String id, @PathVariable String documentName){

        try {
            Resource resourceFile = droneAcquisitionFormService.getFile(id, documentName);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resourceFile.getFilename() + "\"").body(resourceFile);
        } catch(StorageFileNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}

