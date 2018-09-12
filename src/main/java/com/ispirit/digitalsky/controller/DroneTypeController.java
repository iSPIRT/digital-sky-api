package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.domain.DroneType;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.service.api.DroneTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

import static com.ispirit.digitalsky.controller.DroneTypeController.DRONE_RESOURCE_BASE_PATH;
import static com.ispirit.digitalsky.util.FileStoreHelper.resolveFileName;

@RestController
@RequestMapping(DRONE_RESOURCE_BASE_PATH)
public class DroneTypeController {

    public static final String DRONE_RESOURCE_BASE_PATH = "/api/droneType";

    private DroneTypeService droneTypeService;

    PasswordEncoder passwordEncoder;

    @Autowired
    public DroneTypeController(DroneTypeService droneTypeService) {
        this.droneTypeService = droneTypeService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDroneType(
            @RequestParam(value="droneType") String droneTypeString,
            @RequestParam(value = "opManualDoc", required = false) MultipartFile opManualDoc,
            @RequestParam(value = "maintenanceGuidelinesDoc", required = false) MultipartFile maintenanceGuidelinesDoc) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            DroneType droneType = mapper.readValue(droneTypeString, DroneType.class);
            appendDocs(droneType,opManualDoc,maintenanceGuidelinesDoc);
            DroneType createdDroneType = droneTypeService.createDroneType(droneType);
            return new ResponseEntity<>(createdDroneType, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDroneType(
            @PathVariable long id,
            @RequestParam(value = "opManualDoc", required = false) MultipartFile opManualDoc,
            @RequestParam(value = "maintenanceGuidelinesDoc", required = false) MultipartFile maintenanceGuidelinesDoc,
            @RequestParam(value="droneType") String droneTypeString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            DroneType droneType = mapper.readValue(droneTypeString, DroneType.class);
            appendDocs(droneType, opManualDoc,maintenanceGuidelinesDoc);
            DroneType updatedDroneType = droneTypeService.updateDroneType(id, droneType);
            return new ResponseEntity<>(updatedDroneType, HttpStatus.OK);
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

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listAll() {

        Collection<?> droneTypes = droneTypeService.getAll();
        return new ResponseEntity<>(droneTypes, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(@PathVariable long id) {

        DroneType droneType = droneTypeService.get(id);
        return new ResponseEntity<>(droneType, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/document/{documentName:.+}", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@PathVariable String id, @PathVariable String documentName){

        try {
            Resource resourceFile = droneTypeService.getFile(id, documentName);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resourceFile.getFilename() + "\"").body(resourceFile);
        } catch(StorageFileNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    private void appendDocs(DroneType droneType, MultipartFile opManualDoc, MultipartFile maintenanceGuidelinesDoc) {

        if(opManualDoc !=null) {
            droneType.setOpManualDoc(opManualDoc);
            droneType.setOpManualDocName(resolveFileName(opManualDoc));
        }

        if(maintenanceGuidelinesDoc !=null) {
            droneType.setMaintenanceGuidelinesDoc(maintenanceGuidelinesDoc);
            droneType.setMaintenanceGuidelinesDocName(resolveFileName(maintenanceGuidelinesDoc));
        }

    }


}
