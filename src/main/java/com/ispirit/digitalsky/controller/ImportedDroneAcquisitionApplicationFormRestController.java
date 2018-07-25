package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.ImportedDroneAcquisitionApplicationForm;
import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplicationForm;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ApplicationFormNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import com.ispirit.digitalsky.service.api.DroneAcquisitionApplicationFormService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

import static com.ispirit.digitalsky.controller.ImportedDroneAcquisitionApplicationFormRestController.IMPORTEDDRONEACQUISITIONFORM_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(IMPORTEDDRONEACQUISITIONFORM_RESOURCE_BASE_PATH)
public class ImportedDroneAcquisitionApplicationFormRestController {

    public static final String IMPORTEDDRONEACQUISITIONFORM_RESOURCE_BASE_PATH = "/api/applicationForm/importedDroneAcquisition";

    private DroneAcquisitionApplicationFormService<ImportedDroneAcquisitionApplicationForm> droneAcquisitionFormService;

    public ImportedDroneAcquisitionApplicationFormRestController(DroneAcquisitionApplicationFormService<ImportedDroneAcquisitionApplicationForm> droneAcquisitionFormService) {

        this.droneAcquisitionFormService = droneAcquisitionFormService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAcquisitionForm(@RequestBody ImportedDroneAcquisitionApplicationForm acquisitionForm) {

        try {
            ImportedDroneAcquisitionApplicationForm createdForm = droneAcquisitionFormService.createDroneAcquisitionApplicationForm(acquisitionForm);
            return new ResponseEntity<>(createdForm, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAcquisitionForm(@PathVariable String id, @RequestParam(value="securityClearanceDoc", required = false) MultipartFile securityClearanceDoc, @RequestParam(value="droneAcquisitionForm") String droneAcquisitionFormString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            //Class<T> acquisitionFormtype = ((Class)((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
            ImportedDroneAcquisitionApplicationForm droneAcquisitionForm = mapper.readValue(droneAcquisitionFormString, ImportedDroneAcquisitionApplicationForm.class);
            ImportedDroneAcquisitionApplicationForm updatedForm = droneAcquisitionFormService.updateDroneAcquisitionApplicationForm(id, droneAcquisitionForm, securityClearanceDoc);
            return new ResponseEntity<>(updatedForm, HttpStatus.OK);
        } catch (JsonGenerationException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (JsonMappingException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ApplicationFormNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
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
            ImportedDroneAcquisitionApplicationForm updatedForm = droneAcquisitionFormService.approveDroneAcquisitionForm(approveRequestBody);
            return new ResponseEntity<>(updatedForm, HttpStatus.OK);
        } catch (ApplicationFormNotFoundException e) {
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

        ImportedDroneAcquisitionApplicationForm applicationForm = droneAcquisitionFormService.get(id);
        return new ResponseEntity<>(applicationForm,HttpStatus.OK);
    }

    @RequestMapping(value = "/getFile/{id}/{fileName}", method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getFile(@PathVariable String id, @PathVariable String fileName){

        try {
            Resource resourceFile = droneAcquisitionFormService.getFile(id, fileName);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resourceFile.getFilename() + "\"").body(resourceFile);
        } catch(StorageFileNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}

