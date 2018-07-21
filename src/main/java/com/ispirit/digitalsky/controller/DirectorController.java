package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.Director;
import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.EntityId;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import com.ispirit.digitalsky.service.api.DirectorService;
import com.ispirit.digitalsky.service.api.OrganizationOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ispirit.digitalsky.controller.DirectorController.DIRECTOR_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(DIRECTOR_RESOURCE_BASE_PATH)
public class DirectorController {

    public static final String DIRECTOR_RESOURCE_BASE_PATH = "/api/orgOperator/{operatorId}/director";

    private DirectorService directorService;

    private OrganizationOperatorService organizationOperatorService;

    @Autowired
    public DirectorController(DirectorService directorService, OrganizationOperatorService organizationOperatorService) {
        this.directorService = directorService;
        this.organizationOperatorService = organizationOperatorService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDirector(@PathVariable(value = "operatorId") long operatorId, @RequestBody Director director) {

        if (!validate(director)) {
            return new ResponseEntity<>(new Errors("Invalid Payload"), HttpStatus.BAD_REQUEST);
        }

        try {
            validateOperatorAccess(operatorId);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        director.setOrganisationId(operatorId);

        Director savedPilotInstance = directorService.createNewDirector(director);

        return new ResponseEntity<>(new EntityId(savedPilotInstance.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDirector(@PathVariable(value = "id") long id, @PathVariable(value = "operatorId") long operatorId, @RequestBody Director directorPayload) {

        try {
            validateOperatorAccess(operatorId);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        Director director = directorService.find(id);

        if (director == null) {
            return new ResponseEntity<>(new Errors("Director details not found"), HttpStatus.NOT_FOUND);
        }

        directorPayload.setOrganisationId(operatorId);

        Director updatedEntity = directorService.updateDirector(id, director);

        return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id, @PathVariable(value = "operatorId") long operatorId) {

        try {
            validateOperatorAccess(operatorId);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        Director director = directorService.find(id);

        if (director == null) {
            return new ResponseEntity<>(new Errors("Director details not found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(director, HttpStatus.OK);
    }

    private void validateOperatorAccess(@PathVariable(value = "operatorId") long operatorId) {

        OrganizationOperator operator = organizationOperatorService.find(operatorId);

        UserPrincipal userPrincipal = UserPrincipal.securityContext();

        if (userPrincipal.getId() != operator.getResourceOwnerId()) {
            throw new UnAuthorizedAccessException();
        }
    }


    private boolean validate(Director director) {
        return true;
    }

}
