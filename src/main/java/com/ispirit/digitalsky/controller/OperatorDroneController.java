package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static com.ispirit.digitalsky.controller.OperatorDroneController.OPERATOR_DRONE_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(OPERATOR_DRONE_RESOURCE_BASE_PATH)
public class OperatorDroneController {

    public static final String OPERATOR_DRONE_RESOURCE_BASE_PATH = "/api/operatorDrone";

    private OperatorDroneService operatorDroneService;
    private UserProfileService userProfileService;

    public OperatorDroneController(OperatorDroneService operatorDroneService, IndividualOperatorRepository individualOperatorRepository, UserProfileService userProfileService ) {
        this.operatorDroneService = operatorDroneService;
        this.userProfileService = userProfileService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> drones() {
        Collection<?> userDrones = operatorDroneService.loadByOperator();
        return new ResponseEntity<>(userDrones, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> drone(@PathVariable("id") long id) {

        OperatorDrone userDrone = operatorDroneService.find(id);
        if (userDrone == null) {
            return new ResponseEntity<>(new Errors("Drone not found"), HttpStatus.NOT_FOUND);
        }

        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());
        if (!profile.owns(userDrone)) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(userDrone, HttpStatus.OK);
    }

}
