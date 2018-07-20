package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.EntityId;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.OperatorProfileAlreadyExist;
import com.ispirit.digitalsky.service.api.IndividualOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ispirit.digitalsky.controller.IndividualOperatorController.INDIVIDUAL_OPERATOR_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(INDIVIDUAL_OPERATOR_RESOURCE_BASE_PATH)
public class IndividualOperatorController {

    public static final String INDIVIDUAL_OPERATOR_RESOURCE_BASE_PATH = "/api/operator";

    private IndividualOperatorService individualOperatorService;

    @Autowired
    public IndividualOperatorController(IndividualOperatorService individualOperatorService) {
        this.individualOperatorService = individualOperatorService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addIndividualOperator(@RequestBody IndividualOperator individualOperator) {

        if (!validate(individualOperator)) {
            return new ResponseEntity<>(new Errors("Invalid Payload"), HttpStatus.BAD_REQUEST);
        }
        UserPrincipal userPrincipal = UserPrincipal.securityContext();

        individualOperator.setResourceOwnerId(userPrincipal.getId());

        try {
            IndividualOperator savedEntity = individualOperatorService.createNewOperator(individualOperator);

            return new ResponseEntity<>(new EntityId(savedEntity.getId()), HttpStatus.OK);
        } catch (OperatorProfileAlreadyExist e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateIndividualOperator(@PathVariable(value = "id") long id, @RequestBody IndividualOperator individualOperatorPayload) {

        IndividualOperator individualOperator = individualOperatorService.find(id);

        if (individualOperator == null) {
            return new ResponseEntity<>(new Errors("Operator details not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != individualOperator.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        individualOperatorPayload.setResourceOwnerId(individualOperator.getResourceOwnerId());
        IndividualOperator updatedEntity = individualOperatorService.updateOperator(id, individualOperatorPayload);

        return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        IndividualOperator individualOperator = individualOperatorService.find(id);

        if (individualOperator == null) {
            return new ResponseEntity<>(new Errors("Operator details not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != individualOperator.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(individualOperator, HttpStatus.OK);
    }


    private boolean validate(IndividualOperator individualOperator) {
        return true;
    }

}
