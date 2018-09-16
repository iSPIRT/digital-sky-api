package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.PilotProfileAlreadyExist;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import com.ispirit.digitalsky.service.api.PilotService;
import com.ispirit.digitalsky.util.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ispirit.digitalsky.controller.PilotController.PILOT_RESOURCE_BASE_PATH;
import static com.ispirit.digitalsky.util.FileStoreHelper.resolveFileName;

@RestController
@RequestMapping(PILOT_RESOURCE_BASE_PATH)
public class PilotController {

    public static final String PILOT_RESOURCE_BASE_PATH = "/api/pilot";

    private PilotService pilotService;
    private CustomValidator validator;
    private ObjectMapper objectMapper;

    @Autowired
    public PilotController(PilotService pilotService, CustomValidator validator, ObjectMapper objectMapper) {
        this.pilotService = pilotService;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPilot(
            @RequestParam(value = "trainingCertificateDocument", required = false) MultipartFile trainingCertificateDocument,
            @RequestParam(value = "pilotPayload") String pilotPayload
        ) {
        try {

            Pilot pilot = objectMapper.readValue(pilotPayload, Pilot.class);

            validator.validate(pilot);

            pilot.setTrainingCertificate(trainingCertificateDocument);
            pilot.setTrainingCertificateDocName(resolveFileName(trainingCertificateDocument));

            UserPrincipal userPrincipal = UserPrincipal.securityContext();

            pilot.setResourceOwnerId(userPrincipal.getId());

            Pilot savedPilotInstance = pilotService.createNewPilot(pilot);
            return new ResponseEntity<>(savedPilotInstance, HttpStatus.CREATED);
        } catch (PilotProfileAlreadyExist e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);

        } catch (IOException e) {
            return new ResponseEntity<>(new Errors("Bad Request"), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePilot(
            @PathVariable(value = "id") long id,
            @RequestParam(value = "trainingCertificateDocument", required = false) MultipartFile trainingCertificateDocument,
            @RequestParam(value = "pilotPayload") String pilotPayload

        ) {
        try {
            Pilot pilot = pilotService.find(id);

            if (pilot == null) {
                return new ResponseEntity<>(new Errors("Pilot details not found"), HttpStatus.NOT_FOUND);
            }

            UserPrincipal userPrincipal = UserPrincipal.securityContext();
            if (userPrincipal.getId() != pilot.getResourceOwnerId()) {
                return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
            }

            Pilot updatedPilotEntity = objectMapper.readValue(pilotPayload, Pilot.class);

            validator.validate(updatedPilotEntity);

            updatedPilotEntity.setResourceOwnerId(pilot.getResourceOwnerId());
            updatedPilotEntity.setTrainingCertificateDocName(resolveFileName(trainingCertificateDocument));
            updatedPilotEntity.setTrainingCertificate(trainingCertificateDocument);

            Pilot updatedEntity = pilotService.updatePilot(id, updatedPilotEntity);

            return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
        } catch (IOException e) {
                return new ResponseEntity<>(new Errors("Bad Request"), HttpStatus.BAD_REQUEST);
            }
        }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        Pilot pilot = pilotService.find(id);

        if (pilot == null) {
            return new ResponseEntity<>(new Errors("Pilot details not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != pilot.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(pilot, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/trainingCertificate", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@PathVariable("id") long pilotId) {

        try {
            Pilot pilot = pilotService.find(pilotId);

            UserPrincipal userPrincipal = UserPrincipal.securityContext();
            if (!userPrincipal.isAdmin() && userPrincipal.getId() != pilot.getResourceOwnerId()) {
                return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
            }
            Resource resourceFile = pilotService.trainingCertificate(pilot);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resourceFile.getFilename() + "\"").body(resourceFile);
        } catch (StorageFileNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}
