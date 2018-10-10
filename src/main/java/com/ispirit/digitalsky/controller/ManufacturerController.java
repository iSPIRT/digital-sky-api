package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ManufacturerExistsException;
import com.ispirit.digitalsky.exception.ValidationException;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.util.CustomValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ispirit.digitalsky.controller.ManufacturerController.MANUFACTURER_RESOURCE_BASE_PATH;
import static com.ispirit.digitalsky.util.FileStoreHelper.resolveFileName;

@RestController
@RequestMapping(MANUFACTURER_RESOURCE_BASE_PATH)
public class ManufacturerController {

    public static final String MANUFACTURER_RESOURCE_BASE_PATH = "/api/manufacturer";

    private final ManufacturerService manufacturerService;
    private final CustomValidator validator;

    @Autowired
    public ManufacturerController(ManufacturerService manufacturerService, CustomValidator validator) {
        this.manufacturerService = manufacturerService;
        this.validator = validator;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createManufacturer(@RequestParam(value = "trustedCertificateDoc", required = false) MultipartFile trustedCertificateDoc,
                                             @RequestParam(value = "manufacturer") String manufacturerString) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            Manufacturer manufacturer = mapper.readValue(manufacturerString, Manufacturer.class);
            validator.validate(manufacturer);
            UserPrincipal userPrincipal = UserPrincipal.securityContext();
            manufacturer.setResourceOwnerId(userPrincipal.getId());
            if (trustedCertificateDoc != null) {
                manufacturer.setTrustedCertificateDoc(trustedCertificateDoc);
                manufacturer.setTrustedCertificateDocName(resolveFileName(trustedCertificateDoc));
            }
            Manufacturer savedEntity = manufacturerService.createNewManufacturer(manufacturer);
            return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);
        } catch (ManufacturerExistsException | ValidationException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateManufacturer(@PathVariable long id, @RequestParam(value = "trustedCertificateDoc", required = false) MultipartFile trustedCertificateDoc,
                                                @RequestParam(value = "manufacturer") String manufacturerString) {
        Manufacturer manufacturer = manufacturerService.find(id);
        if (manufacturer == null) {
            return new ResponseEntity<>(new Errors("Manufacturer not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != manufacturer.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Manufacturer manufacturerPayload = mapper.readValue(manufacturerString, Manufacturer.class);
            validator.validate(manufacturer);
            manufacturerPayload.setResourceOwnerId(manufacturer.getResourceOwnerId());
            if (trustedCertificateDoc != null) {
                manufacturerPayload.setTrustedCertificateDoc(trustedCertificateDoc);
                manufacturerPayload.setTrustedCertificateDocName(resolveFileName(trustedCertificateDoc));
            }

            Manufacturer updatedEntity = manufacturerService.updateManufacturer(id, manufacturerPayload);
            return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Errors(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        Manufacturer manufacturer = manufacturerService.find(id);

        if (manufacturer == null) {
            return new ResponseEntity<>(new Errors("Manufacturer details not found"), HttpStatus.NOT_FOUND);
        }

        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        if (userPrincipal.getId() != manufacturer.getResourceOwnerId()) {
            return new ResponseEntity<>(new Errors("UnAuthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(manufacturer, HttpStatus.OK);
    }

}
