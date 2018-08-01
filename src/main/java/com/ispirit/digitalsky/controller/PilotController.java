package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.EntityId;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.PilotProfileAlreadyExist;
import com.ispirit.digitalsky.service.api.PilotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ispirit.digitalsky.controller.PilotController.PILOT_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(PILOT_RESOURCE_BASE_PATH)
public class PilotController {

    public static final String PILOT_RESOURCE_BASE_PATH = "/api/pilot";

    private PilotService pilotService;

    @Autowired
    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPilot(@Valid @RequestBody Pilot pilot) {

        if (!validate(pilot)) {
            return new ResponseEntity<>(new Errors("Invalid Payload"), HttpStatus.BAD_REQUEST);
        }
        UserPrincipal userPrincipal = UserPrincipal.securityContext();

        pilot.setResourceOwnerId(userPrincipal.getId());

        try {
            Pilot savedPilotInstance = pilotService.createNewPilot(pilot);
            return new ResponseEntity<>(new EntityId(savedPilotInstance.getId()), HttpStatus.OK);
        } catch (PilotProfileAlreadyExist e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);

        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePilot(@PathVariable(value = "id") long id, @Valid @RequestBody Pilot pilotPayload) {

        Pilot pilot = pilotService.find(id);

        if (pilot == null) {
            return new ResponseEntity<>(new Errors("Pilot details not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != pilot.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        pilotPayload.setResourceOwnerId(pilot.getResourceOwnerId());
        Pilot updatedEntity = pilotService.updatePilot(id, pilotPayload);

        return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
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


    private boolean validate(Pilot pilot) {
        return true;
    }

}
