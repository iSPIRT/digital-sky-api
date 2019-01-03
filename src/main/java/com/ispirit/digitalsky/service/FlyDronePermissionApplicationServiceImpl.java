package com.ispirit.digitalsky.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.document.LatLong;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.FlyDronePermissionApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.geojson.GeoJsonObject;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.io.IOUtils.toInputStream;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class FlyDronePermissionApplicationServiceImpl implements FlyDronePermissionApplicationService {


    public static final String PERMISSION_ARTIFACT_XML = "permissionArtifact.xml";

    private FlyDronePermissionApplicationRepository repository;

    private StorageService storageService;

    private AirspaceCategoryService airspaceCategoryService;

    private Configuration configuration;

    private DigitalSignService digitalSignService;

    private OperatorDroneService operatorDroneService;

    private UserProfileService userProfileService;

    private PilotService pilotService;


    public FlyDronePermissionApplicationServiceImpl(
            FlyDronePermissionApplicationRepository repository,
            StorageService storageService,
            AirspaceCategoryService airspaceCategoryService,
            DigitalSignService digitalSignService,
            OperatorDroneService operatorDroneService,
            UserProfileService userProfileService,
            PilotService pilotService, Configuration freemarkerConfiguration) {
        this.repository = repository;
        this.storageService = storageService;
        this.airspaceCategoryService = airspaceCategoryService;
        this.digitalSignService = digitalSignService;
        this.operatorDroneService = operatorDroneService;
        this.userProfileService = userProfileService;
        this.pilotService = pilotService;
        this.configuration = freemarkerConfiguration;
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication createApplication(FlyDronePermissionApplication application) {
        application.setId(null);
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        application.setApplicantId(userPrincipal.getId());
        application.setApplicant(userPrincipal.getUsername());
        OperatorDrone operatorDrone = operatorDroneService.find(application.getDroneId());
        application.setApplicantType(operatorDrone.getOperatorType());
        application.setOperatorId(operatorDrone.getOperatorId());
        setPilotId(application);
        validateFlyArea(application);

        if (application.getStatus() == ApplicationStatus.SUBMITTED) {
            if (application.getFlyArea() == null || application.getFlyArea().isEmpty()) {
                throw new ValidationException(new Errors("Fly Area coordinates required"));
            }
            application.setSubmittedDate(new Date());
            handleSubmit(application);
            FlyDronePermissionApplication document = repository.insert(application);
            generatePermissionArtifact(document);
            return document;
        } else {
            return repository.insert(application);
        }
    }



    @Override
    @Transactional
    public FlyDronePermissionApplication updateApplication(String id, FlyDronePermissionApplication application) throws ApplicationNotFoundException, UnAuthorizedAccessException, StorageException {
        FlyDronePermissionApplication actualForm = repository.findById(id);
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }
        actualForm.setPilotBusinessIdentifier(application.getPilotBusinessIdentifier());
        actualForm.setFlyArea(application.getFlyArea());
        actualForm.setStatus(application.getStatus());
        actualForm.setStartDateTime(application.getStartDateTime());
        actualForm.setEndDateTime(application.getEndDateTime());
        actualForm.setPayloadWeightInKg(application.getPayloadWeightInKg());
        actualForm.setPayloadDetails(application.getPayloadDetails());
        actualForm.setFlightPurpose(application.getFlightPurpose());
        actualForm.setLastModifiedDate(new Date());
        actualForm.setRecurringTimeExpression(application.getRecurringTimeExpression());
        actualForm.setRecurringTimeDurationInMinutes(application.getRecurringTimeDurationInMinutes());
        actualForm.setMaxAltitude(application.getMaxAltitude());
        validateFlyArea(actualForm);
        setPilotId(actualForm);
        if (application.getStatus() == ApplicationStatus.SUBMITTED) {
            if (actualForm.getFlyArea() == null || actualForm.getFlyArea().isEmpty()) {
                throw new ValidationException(new Errors("Fly Area coordinates required"));
            }
            actualForm.setSubmittedDate(new Date());
            handleSubmit(actualForm);
            FlyDronePermissionApplication savedForm = repository.save(actualForm);
            generatePermissionArtifact(actualForm);
            return savedForm;
        } else {
            return repository.save(actualForm);
        }
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication approveApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        FlyDronePermissionApplication actualForm = repository.findById(approveRequestBody.getApplicationFormId());
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        if (actualForm.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new ApplicationNotInSubmittedStatusException();
        }

        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());

        FlyDronePermissionApplication savedForm = repository.save(actualForm);
        return savedForm;
    }

    @Override
    public FlyDronePermissionApplication get(String id) {
        return repository.findById(id);

    }

    @Override
    public Collection<FlyDronePermissionApplication> getApplicationsOfDrone(long droneId) {
        Collection<FlyDronePermissionApplication> applications = repository.findByDroneId(droneId);
        return applications.stream().sorted((o1, o2) -> o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate())).collect(Collectors.toList());

    }

    @Override
    public Collection<FlyDronePermissionApplication> getAllApplications() {
        List<FlyDronePermissionApplication> applications = repository.findAll();
        return applications.stream().sorted((o1, o2) -> o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate())).collect(Collectors.toList());
    }

    @Override

    public Resource getPermissionArtifact(String applicationId) throws StorageFileNotFoundException {
        try {
            return storageService.loadAsResource(applicationId, PERMISSION_ARTIFACT_XML);
        } catch (StorageFileNotFoundException e) {
            generatePermissionArtifact(get(applicationId));
            return storageService.loadAsResource(applicationId, PERMISSION_ARTIFACT_XML);
        }
    }

    void handleSubmit(FlyDronePermissionApplication application) {
        try {
            Map<AirspaceCategory.Type, GeoJsonObject> geoJsonMapByType = airspaceCategoryService.findGeoJsonMapByTypeAndHeight(application.getMaxAltitude());

            GeoJsonObject amberCategories = geoJsonMapByType.get(AirspaceCategory.Type.AMBER);

            boolean result = isFlyAreaIntersects(new ObjectMapper().writeValueAsString(amberCategories), application.getFlyArea());
            if (!result) {
                UserPrincipal userPrincipal = UserPrincipal.securityContext();
                application.setApproverId(userPrincipal.getId());
                application.setApprover(userPrincipal.getUsername());
                application.setApprovedDate(new Date());
                application.setApproverComments("Self approval, within green zone");
                application.setStatus(ApplicationStatus.APPROVED);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void validateFlyArea(FlyDronePermissionApplication application) {
        try {
            if (application.getFlyArea() == null || application.getFlyArea().isEmpty()) return;
            Map<AirspaceCategory.Type, GeoJsonObject> geoJsonMapByType = airspaceCategoryService.findGeoJsonMapByTypeAndHeight(application.getMaxAltitude());

            GeoJsonObject greenCategories = geoJsonMapByType.get(AirspaceCategory.Type.GREEN);
            validateFlyAreaWithin(new ObjectMapper().writeValueAsString(greenCategories), application.getFlyArea());

            GeoJsonObject redCategories = geoJsonMapByType.get(AirspaceCategory.Type.RED);
            validateFlyAreaIntersectsRedZones(new ObjectMapper().writeValueAsString(redCategories), application.getFlyArea());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void checkArea(Double areaMax, List<LatLong> flyArea){
        Coordinate[] flyAreaCoordinates = new Coordinate[flyArea.size()];
        for (int index = 0; index < flyArea.size(); index++) {
            LatLong latLong = flyArea.get(index);
            flyAreaCoordinates[index] = new Coordinate(latLong.getLongitude(), latLong.getLatitude());
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Polygon poly = geometryFactory.createPolygon(flyAreaCoordinates);
        if(Math.toRadians(poly.getArea()) * 6371 * 100 + 0.018 < areaMax)
            return;
        throw new ValidationException(new Errors("Area is greater than defined area limit for the particular airspace region"));
    }

    public void generatePermissionArtifact(FlyDronePermissionApplication application) {
        if (!application.getStatus().equals(ApplicationStatus.APPROVED)) {
            throw new ValidationException(new Errors("Cannot generate permission artifact if application is not approved"));
        }
        String artifactContent = getPermissionArtifactContent(application);
        String signedArtifactContent = digitalSignService.sign(artifactContent);
        storageService.store(PERMISSION_ARTIFACT_XML, signedArtifactContent, application.getId());
    }

    void validateFlyAreaWithin(String greenZones, List<LatLong> flyArea) throws IOException {
        FeatureJSON featureJSON = new FeatureJSON();

        DefaultFeatureCollection featureCollection = (DefaultFeatureCollection) featureJSON.readFeatureCollection(
                toInputStream(greenZones, "UTF-8"));

        Coordinate[] flyAreaCoordinates = new Coordinate[flyArea.size()];
        for (int index = 0; index < flyArea.size(); index++) {
            LatLong latLong = flyArea.get(index);
            flyAreaCoordinates[index] = new Coordinate(latLong.getLongitude(), latLong.getLatitude());
        }

        boolean result = false;
        SimpleFeatureIterator featureIterator = featureCollection.features();
        while (featureIterator.hasNext()) {
            SimpleFeature feature = featureIterator.next();
            Polygon airspaceCategory = (Polygon) feature.getDefaultGeometry();
            Polygon polygon = airspaceCategory.getFactory().createPolygon(flyAreaCoordinates);
//            System.out.println(Math.toRadians(polygon.getArea()) * 6371 * 100 + 0.018);//todo: this is a test area which is wrong, find and fix the correct formula
            if (polygon.within(airspaceCategory)) {
                result = true;
                break;
            }
        }

        if (!result) {
            throw new ValidationException(new Errors("Fly Area should be within Green zone"));
        }
    }

    void validateFlyAreaIntersectsRedZones(String zones, List<LatLong> flyArea) throws IOException {
        if (isFlyAreaIntersects(zones, flyArea)) {
            throw new ValidationException(new Errors("Fly Area cannot intersect with Red Zones"));
        }
    }

    boolean isFlyAreaIntersects(String zones, List<LatLong> flyArea) throws IOException {
        FeatureJSON featureJSON = new FeatureJSON();

        DefaultFeatureCollection featureCollection = (DefaultFeatureCollection) featureJSON.readFeatureCollection(
                toInputStream(zones, "UTF-8"));

        Coordinate[] flyAreaCoordinates = new Coordinate[flyArea.size()];
        for (int index = 0; index < flyArea.size(); index++) {
            LatLong latLong = flyArea.get(index);
            flyAreaCoordinates[index] = new Coordinate(latLong.getLongitude(), latLong.getLatitude());
        }

        boolean result = false;
        SimpleFeatureIterator featureIterator = featureCollection.features();
        while (featureIterator.hasNext()) {
            SimpleFeature feature = featureIterator.next();
            Polygon airspaceCategory = (Polygon) feature.getDefaultGeometry();
            Polygon polygon = airspaceCategory.getFactory().createPolygon(flyAreaCoordinates);
            if (polygon.intersects(airspaceCategory)) {
                result = true;
                break;
            }
        }

        return result;
    }

    private String getPermissionArtifactContent(FlyDronePermissionApplication application) {
        try {
            Template template = configuration.getTemplate("permissionArtifactXmlTemplate.ftl");
            HashMap<Object, Object> parameters = new HashMap<>();

            OperatorDrone operatorDrone = operatorDroneService.find(application.getDroneId());
            String operatorId = userProfileService.resolveOperatorBusinessIdentifier(operatorDrone.getOperatorType(), operatorDrone.getOperatorId());

            parameters.put("operatorId", operatorId);
            parameters.put("pilotId", application.getPilotBusinessIdentifier());
            parameters.put("uinNumber", operatorDrone.getUinApplicationId());
            parameters.put("purposeOfFlight", application.getFlightPurpose());
            parameters.put("payloadWeightInKg", application.getPayloadWeightInKg());
            parameters.put("payloadDetails", application.getPayloadDetails());
            parameters.put("startDateTime", application.getStartDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
            parameters.put("endDateTime", application.getEndDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
            parameters.put("recurrenceTimeExpression", application.getRecurringTimeExpression());
            parameters.put("recurrenceTimeExpressionType", application.getRecurringTimeExpressionType());
            parameters.put("recurringTimeDurationInMinutes", application.getRecurringTimeDurationInMinutes());
            parameters.put("maxAltitude",application.getMaxAltitude());

            List<String> coordinates = new ArrayList<>();

            for (LatLong latLong : application.getFlyArea()) {
                coordinates.add(String.format("<Coordinate latitude=\"%s\" longitude=\"%s\"/>", latLong.getLatitude(), latLong.getLongitude()));
            }
            parameters.put("coordinates", StringUtils.join(coordinates, ""));


            StringWriter stringWriter = new StringWriter();
            template.process(parameters, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPilotId(FlyDronePermissionApplication application) {
        if (isEmpty(application.getPilotBusinessIdentifier())) {
            throw new ValidationException(new Errors("Pilot Identifier required"));
        }

        Pilot pilot = pilotService.findByBusinessIdentifier(application.getPilotBusinessIdentifier());

        if (pilot == null) {
            throw new ValidationException(new Errors("Invalid Pilot Identifier"));
        }
        application.setPilotId(pilot.getId());
    }

}
