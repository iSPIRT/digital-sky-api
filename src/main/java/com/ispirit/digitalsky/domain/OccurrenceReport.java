package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_occurrence_report")
public class OccurrenceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "OPERATOR_DRONE_ID")
    @NotNull
    private long operatorDroneId;

    @Column(name = "OCCURRENCE_TIMESTAMP")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    @NotNull
    private LocalDateTime occurrenceTimestamp;

    @Column(name = "PLACE_OF_OCCURRENCE")
    @NotNull
    private String placeOfOccurrence;

    @Column(name = "OCCURRENCE_LATITUDE")
    @NotNull
    private String latitude;

    @Column(name = "OCCURRENCE_LONGITUDE")
    @NotNull
    private String longitude;

    @Column(name = "PHASE_OF_FLIGHT")
    @NotNull
    private String phaseOfFlight;

    @Column(name = "TYPE_OF_OPERATION")
    @NotNull
    private String typeOfOperation;

    @Column(name = "COLOR_OF_RPA")
    @NotNull
    private String colorOfRpa;

    @Column(name = "RPA_DAMAGE_DETAILS")
    private String rpaDamageDetails;

    @Column(name = "PROPERTY_DAMAGE_DETAILS")
    private String propertyDamageDetails;

    @Column(name = "DETAILS_OF_INJURY")
    private String detailsOfInjury;

    @Column(name = "DETAILS_OF_PILOT")
    @NotNull
    private String pilotDetails;

    @Column(name = "UAOP_NUMBER")
    @NotNull
    private String uaopNUmber;

    @Column(name = "OCCURRENCE_DESCRIPTION")
    @NotNull
    private String occurrenceDescription;

    @Column(name = "DISTANCE_FROM_AIRCRAFT")
    private String distanceFromAircraft;

    @Column(name = "DISTANCE_FROM_HELIPAD")
    private String distanceFromHelipad;

    @Column(name = "PROXIMITY_FROM_DANGER_ZONE")
    private String proximityFromDangerZone;

    @Column(name = "CREATED_BY_ID")
    @JsonIgnore
    private long createdById;

    @Column(name = "CREATED_TIMESTAMP")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @JsonIgnore
    private LocalDateTime createdTimestamp = LocalDateTime.now();

    public long getOperatorDroneId() {
        return operatorDroneId;
    }

    public void setCreatedById(long createdById) {
        this.createdById = createdById;
    }

    public OccurrenceReport() {
    }

    public OccurrenceReport(long operatorDroneId,
                            LocalDateTime occurrenceTimestamp, String placeOfOccurrence, String latitude,
                            String longitude, String phaseOfFlight, String typeOfOperation, String colorOfRpa,
                            String rpaDamageDetails, String propertyDamageDetails, String detailsOfInjury,
                            String pilotDetails, String uaopNUmber, String occurrenceDescription,
                            String distanceFromAircraft, String distanceFromHelipad, String proximityFromDangerZone) {
        this.operatorDroneId = operatorDroneId;
        this.occurrenceTimestamp = occurrenceTimestamp;
        this.placeOfOccurrence = placeOfOccurrence;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phaseOfFlight = phaseOfFlight;
        this.typeOfOperation = typeOfOperation;
        this.colorOfRpa = colorOfRpa;
        this.rpaDamageDetails = rpaDamageDetails;
        this.propertyDamageDetails = propertyDamageDetails;
        this.detailsOfInjury = detailsOfInjury;
        this.pilotDetails = pilotDetails;
        this.uaopNUmber = uaopNUmber;
        this.occurrenceDescription = occurrenceDescription;
        this.distanceFromAircraft = distanceFromAircraft;
        this.distanceFromHelipad = distanceFromHelipad;
        this.proximityFromDangerZone = proximityFromDangerZone;
    }

    public long getCreatedById() {
        return createdById;
    }
}
