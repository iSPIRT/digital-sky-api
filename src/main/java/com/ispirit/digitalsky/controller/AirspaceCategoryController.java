package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.AirspaceCategory;
import com.ispirit.digitalsky.service.api.AirspaceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ispirit.digitalsky.controller.AirspaceCategoryController.AIRSPACE_CATEGORY_BASE_PATH;

@RestController
@RequestMapping(AIRSPACE_CATEGORY_BASE_PATH)
public class AirspaceCategoryController {

    public static final String AIRSPACE_CATEGORY_BASE_PATH = "/api/airspaceCategory";

    private AirspaceCategoryService airspaceCategoryService;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    public AirspaceCategoryController(AirspaceCategoryService airspaceCategoryService) {
        this.airspaceCategoryService = airspaceCategoryService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addAirspaceCategory(@Valid @RequestBody AirspaceCategory airspaceCategoryPayload) {
        AirspaceCategory airspaceCategory;
        if(airspaceCategoryPayload.getTempStartTime()!=null && airspaceCategoryPayload.getTempEndTime()!=null)
            airspaceCategory = new AirspaceCategory(airspaceCategoryPayload.getName(), airspaceCategoryPayload.getType(), airspaceCategoryPayload.getGeoJson(),airspaceCategoryPayload.getMinAltitude(),airspaceCategoryPayload.getTempStartTime(),airspaceCategoryPayload.getTempEndTime());
        else
            airspaceCategory = new AirspaceCategory(airspaceCategoryPayload.getName(), airspaceCategoryPayload.getType(), airspaceCategoryPayload.getGeoJson(),airspaceCategoryPayload.getMinAltitude());
        AirspaceCategory savedEntity = airspaceCategoryService.createNewAirspaceCategory(airspaceCategory);
        return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAirspaceCategory(@PathVariable(value = "id") long id, @Valid @RequestBody AirspaceCategory airspaceCategoryPayload) {
        AirspaceCategory airspaceCategory;
        if(airspaceCategoryPayload.getTempStartTime()!=null && airspaceCategoryPayload.getTempEndTime()!=null)
            airspaceCategory = new AirspaceCategory(airspaceCategoryPayload.getName(), airspaceCategoryPayload.getType(), airspaceCategoryPayload.getGeoJson(),airspaceCategoryPayload.getMinAltitude(),airspaceCategoryPayload.getTempStartTime(),airspaceCategoryPayload.getTempEndTime());
        else
            airspaceCategory = new AirspaceCategory(airspaceCategoryPayload.getName(), airspaceCategoryPayload.getType(), airspaceCategoryPayload.getGeoJson(),airspaceCategoryPayload.getMinAltitude());
        AirspaceCategory savedEntity = airspaceCategoryService.updateAirspaceCategory(id, airspaceCategory);
        return new ResponseEntity<>(savedEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAirspaceCategory(@PathVariable(value = "id") long id) {
        return new ResponseEntity<>(airspaceCategoryService.find(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllAirspaceCategory() {
        return new ResponseEntity<>(airspaceCategoryService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/listHeight", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllAirspaceCategoryHeight(@RequestParam("maxHeight")Long height) {
        return new ResponseEntity<>(airspaceCategoryService.findAllAboveHeight(height), HttpStatus.OK);
    }

    @RequestMapping(value = "/listHeightTime", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllAirspaceCategoryHeightTime(@RequestParam("maxHeight")Long height,@RequestParam("startTime")String startTime,@RequestParam("endTime")String endTime) {
        return new ResponseEntity<>(airspaceCategoryService.findAllAboveHeightTime(height, LocalDateTime.parse(startTime,formatter),LocalDateTime.parse(endTime,formatter)), HttpStatus.OK);
    }

}
