package com.ispirit.digitalsky.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.util.CustomLocalDateTimeDeSerializer;
import com.ispirit.digitalsky.util.CustomLocalDateTimeSerializer;
import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection = "flyDronePermissionApplications")
@TypeAlias("flyDronePermissionApplications")
public class FlyDronePermissionApplication extends BasicApplication {

    @Field("pilotBusinessIdentifier")
    @NotNull
    private String pilotBusinessIdentifier;

    @Field("pilotId")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long pilotId;


    @Field("flyArea")
    @NotNull
    private List<LatLong> flyArea;

    @Field("droneId")
    @NotNull
    private long droneId;

    @Field("operatorId")
    @NotNull
    private long operatorId;


    @Field("payloadWeightInKg")
    @NotNull
    @Min(value = 0)
    private double payloadWeightInKg;

    @Field("payloadDetails")
    @NotNull
    @Size(max = 500)
    private String payloadDetails;

    @Field("flightPurpose")
    @NotNull
    @Size(max = 500)
    private String flightPurpose;


    @Field("startDateTime")
    @NotNull
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime startDateTime;


    @Field("endDateTime")
    @NotNull
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime endDateTime;

    @Field("recurringTimeExpression")
    @Size(max = 50)
    private String recurringTimeExpression;

    @Field("recurringTimeDurationInMinutes")
    private Long recurringTimeDurationInMinutes;

    @Field("recurringPatternType")
    @Size(max = 50)
    private String recurringTimeExpressionType;

    @Field("maxAltitude")
    private int maxAltitude;

    @Field("fir")
    private String fir;

    @Field("adcNumber")
    private String adcNumber;

    @Field("ficNumber")
    private String ficNumber;


    public FlyDronePermissionApplication() {
        setCreatedDate(new Date());
        setLastModifiedDate(new Date());
        recurringTimeExpressionType = "CRON_QUARTZ";
    }

    public List<LatLong> getFlyArea() {
        return flyArea;
    }

    public void setFlyArea(List<LatLong> flyArea) {
        this.flyArea = flyArea;
    }

    public long getDroneId() {
        return droneId;
    }

    public void setDroneId(long droneId) {
        this.droneId = droneId;
    }

    public double getPayloadWeightInKg() {
        return payloadWeightInKg;
    }

    public void setPayloadWeightInKg(double payloadWeightInKg) {
        this.payloadWeightInKg = payloadWeightInKg;
    }

    public String getPayloadDetails() {
        return payloadDetails;
    }

    public void setPayloadDetails(String payloadDetails) {
        this.payloadDetails = payloadDetails;
    }

    public String getFlightPurpose() {
        return flightPurpose;
    }

    public void setFlightPurpose(String flightPurpose) {
        this.flightPurpose = flightPurpose;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public String getRecurringTimeExpression() {
        return recurringTimeExpression;
    }

    public void setRecurringTimeExpression(String recurringTimeExpression) {
        this.recurringTimeExpression = recurringTimeExpression;
    }

    public String getRecurringTimeExpressionType() {
        return recurringTimeExpressionType;
    }

    public void setRecurringTimeExpressionType(String recurringTimeExpressionType) {
        this.recurringTimeExpressionType = recurringTimeExpressionType;
    }

    public Long getRecurringTimeDurationInMinutes() {
        return recurringTimeDurationInMinutes;
    }

    public void setRecurringTimeDurationInMinutes(Long recurringTimeDurationInMinutes) {
        this.recurringTimeDurationInMinutes = recurringTimeDurationInMinutes;
    }

    public String getPilotBusinessIdentifier() {
        return pilotBusinessIdentifier;
    }

    public void setPilotBusinessIdentifier(String pilotBusinessIdentifier) {
        this.pilotBusinessIdentifier = pilotBusinessIdentifier;
    }

    public void setPilotId(long pilotId) {
        this.pilotId = pilotId;
    }

    public int getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(int maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public String getFir() {
        return fir;
    }

    public void setFir(String fir) {
        this.fir = fir;
    }

    public String getAdcNumber() {
        return adcNumber;
    }

    public void setAdcNumber(String adcNumber) {
        this.adcNumber = adcNumber;
    }

    public String getFicNumber() {
        return ficNumber;
    }

    public void setFicNumber(String ficNumber) {
        this.ficNumber = ficNumber;
    }
}
