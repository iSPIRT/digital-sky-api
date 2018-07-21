package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.EntityId;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.OperatorProfileAlreadyExist;
import com.ispirit.digitalsky.service.api.IndividualOperatorService;
import com.ispirit.digitalsky.service.api.OrganizationOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ispirit.digitalsky.controller.IndividualOperatorController.INDIVIDUAL_OPERATOR_RESOURCE_BASE_PATH;
import static com.ispirit.digitalsky.controller.OrganizationOperatorController.ORGANIZATION_OPERATOR_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(ORGANIZATION_OPERATOR_RESOURCE_BASE_PATH)
public class OrganizationOperatorController {

    public static final String ORGANIZATION_OPERATOR_RESOURCE_BASE_PATH = "/api/orgOperator";

    private OrganizationOperatorService organizationOperatorService;

    @Autowired
    public OrganizationOperatorController(OrganizationOperatorService organizationOperatorService) {
        this.organizationOperatorService = organizationOperatorService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOrgOperator(@RequestBody OrganizationOperator organizationOperator) {

        if (!validate(organizationOperator)) {
            return new ResponseEntity<>(new Errors("Invalid Payload"), HttpStatus.BAD_REQUEST);
        }
        UserPrincipal userPrincipal = UserPrincipal.securityContext();

        organizationOperator.setResourceOwnerId(userPrincipal.getId());

        try {
            OrganizationOperator savedEntity = organizationOperatorService.createNewOperator(organizationOperator);

            return new ResponseEntity<>(new EntityId(savedEntity.getId()), HttpStatus.OK);
        } catch (OperatorProfileAlreadyExist e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOrgOperator(@PathVariable(value = "id") long id, @RequestBody OrganizationOperator organizationOperatorPayload) {

        OrganizationOperator organizationOperator = organizationOperatorService.find(id);

        if (organizationOperator == null) {
            return new ResponseEntity<>(new Errors("Operator details not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != organizationOperator.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        organizationOperatorPayload.setResourceOwnerId(organizationOperator.getResourceOwnerId());
        OrganizationOperator updatedEntity = organizationOperatorService.updateOperator(id, organizationOperatorPayload);

        return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        OrganizationOperator organizationOperator = organizationOperatorService.find(id);

        if (organizationOperator == null) {
            return new ResponseEntity<>(new Errors("Operator details not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != organizationOperator.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(organizationOperator, HttpStatus.OK);
    }


    private boolean validate(OrganizationOperator organizationOperator) {
        return true;
    }

}
