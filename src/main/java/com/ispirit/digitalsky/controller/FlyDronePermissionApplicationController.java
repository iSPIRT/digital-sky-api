package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.service.api.FlyDronePermissionApplicationService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import com.ispirit.digitalsky.util.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

import static com.ispirit.digitalsky.controller.FlyDronePermissionApplicationController.APPLICATION_RESOURCE_BASE_PATH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@RestController
@RequestMapping(APPLICATION_RESOURCE_BASE_PATH)
public class FlyDronePermissionApplicationController {

    public static final String APPLICATION_RESOURCE_BASE_PATH = "/api/applicationForm/flyDronePermissionApplication";

    private FlyDronePermissionApplicationService service;
    private OperatorDroneService operatorDroneService;
    private UserProfileService userProfileService;
    private CustomValidator validator;

    @Autowired
    public FlyDronePermissionApplicationController(FlyDronePermissionApplicationService service, OperatorDroneService operatorDroneService, UserProfileService userProfileService, CustomValidator validator) {
        this.service = service;
        this.operatorDroneService = operatorDroneService;
        this.userProfileService = userProfileService;
        this.validator = validator;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createApplication(@RequestBody FlyDronePermissionApplication application) {
        validateDroneId(application.getDroneId());
        if (application.isSubmitted()) {
            validator.validate(application);
        }
        FlyDronePermissionApplication savedApplication = service.createApplication(application);
        return new ResponseEntity<>(savedApplication, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateApplication(@PathVariable String id, @RequestBody FlyDronePermissionApplication application) {

        validateDroneId(application.getDroneId());

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        FlyDronePermissionApplication currentApplication = service.get(id);

        if(currentApplication == null) throw new EntityNotFoundException("FlyDronePermissionApplication", id);

        if (userPrincipal.getId() != currentApplication.getApplicantId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        if (!currentApplication.canBeModified()) {
            return new ResponseEntity<>(new Errors("Application not in draft status, cannot be modified"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (application.isSubmitted()) {
            validator.validate(application);
        }
        FlyDronePermissionApplication savedApplication = service.updateApplication(id, application);
        return new ResponseEntity<>(savedApplication, HttpStatus.OK);

    }


    @RequestMapping(value = "/approve/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveApplication(@PathVariable String id, @Valid @RequestBody ApproveRequestBody approveRequestBody) {
        try {
            FlyDronePermissionApplication application = service.approveApplication(approveRequestBody);
            return new ResponseEntity<>(application, HttpStatus.OK);
        } catch (ApplicationNotFoundException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnAuthorizedAccessException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (ApplicationNotInSubmittedStatus e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listApplications(@RequestParam(required = false, value = "droneId") String droneIdString) {
        long droneId = 0;
        Collection<FlyDronePermissionApplication> applications;
        if (isNotEmpty(droneIdString)) {
            try {
                droneId = Long.parseLong(droneIdString);
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(new Errors("Invalid Drone Id"), HttpStatus.BAD_REQUEST);
            }
        }
        if (droneId != 0) {
            validateDroneId(droneId);
            applications = service.getApplicationsOfDrone(droneId);
            return new ResponseEntity<>(applications, HttpStatus.OK);
        }
        return new ResponseEntity<>(new Errors("Invalid Drone Id"), HttpStatus.BAD_REQUEST);
    }

     private void validateDroneId(long droneId) {
        OperatorDrone operatorDrone = operatorDroneService.find(droneId);
        if (operatorDrone == null) {
            throw new ValidationException(new Errors("Invalid Drone Id"));
        }

        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());
        if (!profile.owns(operatorDrone)) {
            throw new UnAuthorizedAccessException();
        }
    }

}

