package com.ispirit.digitalsky.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.document.LatLong;
import com.ispirit.digitalsky.document.UAOPApplication;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private List<FlightInformationRegion> firs;

    private AdcNumberServiceImpl adcNumberServiceImpl;

    private FicNumberServiceImpl ficNumberServiceImpl;

    private UAOPApplicationService uaopApplicationService;

    public static final int SUNRISE_HOUR = 5;

    public static final int SUNRISE_SUNSET_MINUTE = 30;

    public static final int SUNSET_HOUR = 19;

    public static final int MINIMUM_DAYS_BEFORE_PERMISSION_APPLY=1;

    public static final int MAXIMUM_DAYS_FOR_PERMISSION_APPLY=5;

    public static final int MAXIMUM_FLIGHT_AGL_IN_FT=400;

    public static final int MAXIMUM_AUTO_PERM_MICRO_ALTITUDE_AGL_FT=200;

    public static final int MAXIMUM_AUTO_PERM_NANO_ALTITUDE_AGL_FT=50;

    public static final double MAXIMUM_FLIGHT_AREA_SQ_KM=3.14159;

    private long maxEnduranceOfDrone;

    private String typeOfDrone;

    public FlyDronePermissionApplicationServiceImpl(
            FlyDronePermissionApplicationRepository repository,
            StorageService storageService,
            AirspaceCategoryService airspaceCategoryService,
            DigitalSignService digitalSignService,
            OperatorDroneService operatorDroneService,
            UserProfileService userProfileService,
            PilotService pilotService, Configuration freemarkerConfiguration,List<FlightInformationRegion> firs, AdcNumberServiceImpl adcNumberServiceImpl, FicNumberServiceImpl ficNumberServiceImpl, UAOPApplicationService uaopApplicationService) {
        this.repository = repository;
        this.storageService = storageService;
        this.airspaceCategoryService = airspaceCategoryService;
        this.digitalSignService = digitalSignService;
        this.operatorDroneService = operatorDroneService;
        this.userProfileService = userProfileService;
        this.pilotService = pilotService;
        this.configuration = freemarkerConfiguration;
        this.firs=firs;
        this.adcNumberServiceImpl = adcNumberServiceImpl;
        this.ficNumberServiceImpl = ficNumberServiceImpl;
        this.uaopApplicationService = uaopApplicationService;
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication createApplication(FlyDronePermissionApplication application) {
        application.setId(null);
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        application.setApplicantId(userPrincipal.getId());
        application.setApplicant(userPrincipal.getUsername());
        application.setApplicantEmail(userPrincipal.getEmail());
        OperatorDrone operatorDrone = operatorDroneService.find(application.getDroneId());

        maxEnduranceOfDrone = (long) operatorDrone.getDroneType().getMaxEndurance();
        typeOfDrone = operatorDrone.getDroneType().getDroneCategoryType().getValue();
        application.setApplicantType(operatorDrone.getOperatorType());
        application.setOperatorId(operatorDrone.getOperatorId());
        setPilotId(application);
        application.setMaxEndurance(maxEnduranceOfDrone);
        application.setDroneType(typeOfDrone);
        application.setUin(operatorDrone.getUinNo());
        checkMaxHeight(application);
        checkTimeWithinSunriseSunset(application);
        checkWithinAday(application);
        checkWithinMaxDays(application);
        checkArea(application.getFlyArea());
        validateFlyArea(application);

        if (application.getStatus() == ApplicationStatus.SUBMITTED) {
            FlightInformationRegion matchingFir = getFirForFlightArea(application);
            application.setSubmittedDate(new Date());
            handleSubmit(application);
            application.setFir(matchingFir.getName());
            FlyDronePermissionApplication document = repository.insert(application);
            return document;
        } else {
            return repository.insert(application);
        }
    }

    void checkTimeWithinSunriseSunset(FlyDronePermissionApplication application) {
        LocalDateTime earliestPossibleTime = LocalDateTime.of(
            LocalDate.of(application.getStartDateTime().getYear(),application.getStartDateTime().getMonthValue(),application.getStartDateTime().getDayOfMonth()),
            LocalTime.of(SUNRISE_HOUR,SUNRISE_SUNSET_MINUTE,00));
        LocalDateTime lastPossibleTime = LocalDateTime.of(
            LocalDate.of(application.getStartDateTime().getYear(),application.getStartDateTime().getMonthValue(),application.getStartDateTime().getDayOfMonth()),
            LocalTime.of(SUNSET_HOUR,SUNRISE_SUNSET_MINUTE,00));
        if(!(application.getStartDateTime().compareTo(earliestPossibleTime) > 0 && application.getEndDateTime().compareTo(lastPossibleTime)<0))
            throw new ValidationException(new Errors("Flight time not within sunrise and sunset"));
    }

    void checkWithinAday(FlyDronePermissionApplication application){
        LocalDateTime checkDay=LocalDateTime.now().plusDays(MINIMUM_DAYS_BEFORE_PERMISSION_APPLY);
        if(!(application.getStartDateTime().compareTo(checkDay)>0))
            throw new ValidationException(new Errors("Flight time is before 1 day from now"));
    }

    void checkWithinMaxDays(FlyDronePermissionApplication application){
        LocalDateTime checkDay=LocalDateTime.now().plusDays(MAXIMUM_DAYS_FOR_PERMISSION_APPLY);
        if(application.getStartDateTime().compareTo(checkDay)>0)
            throw new ValidationException(new Errors("Flight time is beyond maximum days from now"));
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
        checkMaxHeight(application);
        checkTimeWithinSunriseSunset(application);
        checkWithinAday(application);
        checkWithinMaxDays(application);
        checkArea(application.getFlyArea());
        validateFlyArea(actualForm);
        setPilotId(actualForm);
        if (application.getStatus() == ApplicationStatus.SUBMITTED) {
            FlightInformationRegion matchingFir = getFirForFlightArea(actualForm);
            actualForm.setSubmittedDate(new Date());
            handleSubmit(actualForm);
            actualForm.setFir(matchingFir.getName());
            return repository.save(actualForm);
        } else {
            return repository.save(actualForm);
        }
    }

    public FlightInformationRegion getFirForFlightArea(FlyDronePermissionApplication application){
        if (application.getFlyArea() == null || application.getFlyArea().isEmpty()) {
            throw new ValidationException(new Errors("Fly Area coordinates required"));
        }
        FlightInformationRegion matchingFir = null;
        for(int i =0;i<firs.size();i++){
            try{
                if(isFlyAreaIntersects(firs.get(i).getGeoJsonString(),application.getFlyArea())) {
                    matchingFir = firs.get(i);
                    break;
                }
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        if(matchingFir==null){
//                throw new RuntimeException("Not under any FIR"); todo: this comment has to be removed later as it has to be one of the 4 FIRs
            matchingFir=firs.get(firs.size()-1);
        }
        return matchingFir;
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

        String ficNumber = ficNumberServiceImpl.generateNewFicNumber(actualForm);
        String adcNumber = adcNumberServiceImpl.generateNewAdcNumber(actualForm);
        actualForm.setFicNumber(ficNumber);
        actualForm.setAdcNumber(adcNumber);
        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());
        generatePermissionArtifactWithAdcAndFic(actualForm,ficNumber,adcNumber);
        FlyDronePermissionApplication savedForm = repository.save(actualForm);
        return savedForm;
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication approveByAtcApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        FlyDronePermissionApplication actualForm = repository.findById(approveRequestBody.getApplicationFormId());
        String ficNumber = ficNumberServiceImpl.generateNewFicNumber(actualForm);
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        if (actualForm.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new ApplicationNotInSubmittedStatusException();
        }
        actualForm.setFicNumber(ficNumber);
        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());

        FlyDronePermissionApplication savedForm = repository.save(actualForm);
        return savedForm;
    }

    @Override
    @Transactional
    public FlyDronePermissionApplication approveByAfmluApplication(ApproveRequestBody approveRequestBody) throws ApplicationNotFoundException, UnAuthorizedAccessException {
        UserPrincipal userPrincipal = UserPrincipal.securityContext();
        FlyDronePermissionApplication actualForm = repository.findById(approveRequestBody.getApplicationFormId());
        if (actualForm == null) {
            throw new ApplicationNotFoundException();
        }

        if (actualForm.getStatus() != ApplicationStatus.APPROVEDBYATC) {
            throw new ApplicationNotApprovedByAtc();
        }

        String adcNumber = adcNumberServiceImpl.generateNewAdcNumber(actualForm);
        actualForm.setAdcNumber(adcNumber);

        actualForm.setApproverId(userPrincipal.getId());
        actualForm.setApprover(userPrincipal.getUsername());
        actualForm.setApprovedDate(new Date());
        actualForm.setApproverComments(approveRequestBody.getComments());
        actualForm.setStatus(approveRequestBody.getStatus());
        generatePermissionArtifactWithAdcAndFic(actualForm,actualForm.getFicNumber(),adcNumber);
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

    void checkMaxHeight(FlyDronePermissionApplication application){
        if(application.getMaxAltitude()>MAXIMUM_FLIGHT_AGL_IN_FT){
            throw new ValidationException(new Errors("Altitude of flight is more than 400ft which is beyond allowed airspace."));
        }
    }

    void handleSubmit(FlyDronePermissionApplication application) {
        try {
            Map<AirspaceCategory.Type, GeoJsonObject> geoJsonMapByType = airspaceCategoryService.findGeoJsonMapByTypeAndHeightAndTime(application.getMaxAltitude(),application.getStartDateTime(),application.getEndDateTime());

            GeoJsonObject amberCategories = geoJsonMapByType.get(AirspaceCategory.Type.AMBER);

            boolean result = isFlyAreaIntersects(new ObjectMapper().writeValueAsString(amberCategories), application.getFlyArea());
            if (!result && !droneCategoryRegulationsCheck(application)) {
                UserPrincipal userPrincipal = UserPrincipal.securityContext();
                application.setApproverId(userPrincipal.getId());
                application.setApprover(userPrincipal.getUsername());
                application.setApprovedDate(new Date());
                application.setApproverComments("Self approval, within green zone");
                application.setStatus(ApplicationStatus.APPROVED);
                generatePermissionArtifact(application);
            }
            else{
                UserPrincipal userPrincipal = UserPrincipal.securityContext();
                if(droneCategoryRegulationsCheck(application)) {
                    Collection<UAOPApplication> uaopApplicationCollection = uaopApplicationService.getApplicationsOfApplicant(userPrincipal.getId());
                    for(UAOPApplication app: uaopApplicationCollection){
                        if (!app.getStatus().equals(ApplicationStatus.APPROVED))
                            throw new RuntimeException("You need to have a UAOP approved account to fly with these conditions");
                    }
                }
                application.setStatus(ApplicationStatus.SUBMITTED);
                application.setApproverId(userPrincipal.getId());
                application.setApprover(userPrincipal.getUsername());
                application.setApprovedDate(new Date());
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
            Map<AirspaceCategory.Type, GeoJsonObject> geoJsonMapByType = airspaceCategoryService.findGeoJsonMapByTypeAndHeightAndTime(application.getMaxAltitude(),application.getStartDateTime(),application.getEndDateTime());

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

    void checkArea(List<LatLong> flyArea){
        if (flyArea == null || flyArea.isEmpty()) return;
        Coordinate[] flyAreaCoordinates = new Coordinate[flyArea.size()];
        for (int index = 0; index < flyArea.size(); index++) {
            LatLong latLong = flyArea.get(index);
            flyAreaCoordinates[index] = new Coordinate(latLong.getLongitude(), latLong.getLatitude());
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Polygon poly = geometryFactory.createPolygon(flyAreaCoordinates);
        if(Math.toRadians(poly.getArea()) * 6371 * 100 + 0.018 < MAXIMUM_FLIGHT_AREA_SQ_KM)
            return;
        throw new ValidationException(new Errors("Area is greater than defined area limit for the particular airspace region"));
    }

    boolean droneCategoryRegulationsCheck(FlyDronePermissionApplication application){
        OperatorDrone operatorDrone = operatorDroneService.find(application.getDroneId());
        return operatorDrone.getDroneType().getDroneCategoryType().equals(DroneCategoryType.SMALL)
            || operatorDrone.getDroneType().getDroneCategoryType().equals(DroneCategoryType.MEDIUM)
            || operatorDrone.getDroneType().getDroneCategoryType().equals(DroneCategoryType.LARGE)
            || (operatorDrone.getDroneType().getDroneCategoryType().equals(DroneCategoryType.MICRO) && application.getMaxAltitude()>MAXIMUM_AUTO_PERM_MICRO_ALTITUDE_AGL_FT)
            || (operatorDrone.getDroneType().getDroneCategoryType().equals(DroneCategoryType.NANO) && application.getMaxAltitude()>MAXIMUM_AUTO_PERM_NANO_ALTITUDE_AGL_FT);
    }

    public void generatePermissionArtifact(FlyDronePermissionApplication application) {
        String artifactContent = getPermissionArtifactContent(application);
        String signedArtifactContent = digitalSignService.sign(artifactContent);
        storageService.store(PERMISSION_ARTIFACT_XML, signedArtifactContent, application.getId());
    }

    public void generatePermissionArtifactWithAdcAndFic(FlyDronePermissionApplication application,String ficNumber,String adcNumber) {
        String artifactContent = getPermissionArtifactContentWithFicAdc(application,ficNumber,adcNumber);
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
            if (polygon.intersects(airspaceCategory)) {//todo check if this would be intersects or should be contains
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

            appendCommonparameters(parameters,application,operatorId,operatorDrone);

            StringWriter stringWriter = new StringWriter();
            template.process(parameters, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String getPermissionArtifactContentWithFicAdc(FlyDronePermissionApplication application, String ficNumber, String adcNumber ) {
        try {
            Template template = configuration.getTemplate("permissionArtifactFicAdcXmlTemplate.ftl");
            HashMap<Object, Object> parameters = new HashMap<>();

            OperatorDrone operatorDrone = operatorDroneService.find(application.getDroneId());
            String operatorId = userProfileService.resolveOperatorBusinessIdentifier(operatorDrone.getOperatorType(), operatorDrone.getOperatorId());

            appendCommonparameters(parameters,application,operatorId,operatorDrone);
            parameters.put("adcNumber",adcNumber);
            parameters.put("ficNumber",ficNumber);

            StringWriter stringWriter = new StringWriter();
            template.process(parameters, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void appendCommonparameters(HashMap<Object, Object> parameters, FlyDronePermissionApplication application, String operatorId, OperatorDrone operatorDrone) {
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
