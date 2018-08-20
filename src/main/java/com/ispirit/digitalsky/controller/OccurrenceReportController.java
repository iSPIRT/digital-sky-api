package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.OccurrenceReport;
import com.ispirit.digitalsky.domain.OperatorDrone;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.service.api.OccurrenceReportService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ispirit.digitalsky.controller.OccurrenceReportController.OR_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(OR_RESOURCE_BASE_PATH)
public class OccurrenceReportController {

    public static final String OR_RESOURCE_BASE_PATH = "/api/occurrenceReport";
    private OccurrenceReportService occurrenceReportService;
    private OperatorDroneService operatorDroneService;
    private UserProfileService userProfileService;

    @Autowired
    public OccurrenceReportController(OccurrenceReportService occurrenceReportService, OperatorDroneService operatorDroneService, UserProfileService userProfileService) {

        this.occurrenceReportService = occurrenceReportService;
        this.operatorDroneService = operatorDroneService;
        this.userProfileService = userProfileService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody OccurrenceReport occurrenceReport) {

        OperatorDrone operatorDrone = operatorDroneService.find(occurrenceReport.getOperatorDroneId());
        if (operatorDrone == null) {
            return new ResponseEntity<>(new Errors("Invalid Drone Id"), HttpStatus.BAD_REQUEST);
        }

        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());
        if (!profile.owns(operatorDrone)) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        occurrenceReport.setCreatedById(UserPrincipal.securityContext().getId());

        OccurrenceReport savedEntity = occurrenceReportService.createNew(occurrenceReport);

        return new ResponseEntity<>(savedEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {

        OccurrenceReport occurrenceReport = occurrenceReportService.find(id);

        if (occurrenceReport == null) {
            return new ResponseEntity<>(new Errors("Occurrence report not found"), HttpStatus.NOT_FOUND);
        }

        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());
        OperatorDrone operatorDrone = operatorDroneService.find(occurrenceReport.getOperatorDroneId());

        if (!profile.owns(operatorDrone)) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(occurrenceReport, HttpStatus.OK);
    }

    @RequestMapping(value = "/drone/{id}/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> list(@PathVariable("id") long droneId) {

        OperatorDrone operatorDrone = operatorDroneService.find(droneId);
        if (operatorDrone == null) {
            return new ResponseEntity<>(new Errors("Invalid Drone Id"), HttpStatus.BAD_REQUEST);
        }

        UserProfile profile = userProfileService.profile(UserPrincipal.securityContext().getId());
        if (!profile.owns(operatorDrone)) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        List<OccurrenceReport> occurrenceReportList = occurrenceReportService.findByDroneId(droneId);
        return new ResponseEntity<>(occurrenceReportList, HttpStatus.OK);
    }
}
