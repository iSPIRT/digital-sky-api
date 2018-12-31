package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;
import org.geojson.GeoJsonObject;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_airspace_category")
public class AirspaceCategory {

    public enum Type {
        RED(2), AMBER(1), GREEN(0);

        private int layerOrder;

        Type(int layerOrder) {
            this.layerOrder = layerOrder;
        }

        public int getLayerOrder() {
            return layerOrder;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "NAME")
    @NotNull
    @Size(max = 50)
    private String name;

    @Column(name = "CATEGORY_TYPE")
    @NotNull
    private Type type;

    @Transient
    @NotNull
    private GeoJsonObject geoJson;

    @Column(name = "GEO_JSON")
    @JsonIgnore
    private String geoJsonString;

    @Column(name = "CREATED_BY_ID")
    @JsonIgnore
    private long createdById;

    @Column(name = "MODIFIED_BY_ID")
    @JsonIgnore
    private long modifiedById;

    @Column(name = "CREATED_DATE")
    @JsonIgnore
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime createdDate;

    @Column(name = "MODIFIED_DATE")
    @JsonIgnore
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime modifiedDate;

    @Column(name = "MIN_ALTITUDE")
    @Value("${minAltitude:0}")
    private long minAltitude;



    private AirspaceCategory() {
        //for serialization and de-serialization
    }

    public AirspaceCategory(String name, Type type, GeoJsonObject geoJson) {
        this.name = name;
        this.type = type;
        this.geoJson = geoJson;
        try {
            this.geoJsonString = new ObjectMapper().writeValueAsString(geoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
    }

    public AirspaceCategory(String name, Type type, GeoJsonObject geoJson, long minAltitude) {
        this.name = name;
        this.type = type;
        this.geoJson = geoJson;
        try {
            this.geoJsonString = new ObjectMapper().writeValueAsString(geoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
        this.minAltitude=minAltitude;
    }

    public AirspaceCategory(String name, Type type, String geoJsonString) {
        this.name = name;
        this.type = type;
        this.geoJsonString = geoJsonString;
        try {
            this.geoJson = new ObjectMapper().readValue(geoJsonString, GeoJsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
    }

    public AirspaceCategory(String name, Type type, String geoJsonString, long minAltitude) {
        this.name = name;
        this.type = type;
        this.geoJsonString = geoJsonString;
        try {
            this.geoJson = new ObjectMapper().readValue(geoJsonString, GeoJsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();
        this.minAltitude=minAltitude;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getGeoJsonString() {
        return geoJsonString;
    }

    public GeoJsonObject getGeoJson() {
        return geoJson;
    }

    public void setCreatedById(long createdById) {
        this.createdById = createdById;
    }

    public void setModifiedById(long modifiedById) {
        this.modifiedById = modifiedById;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getCreatedById() {
        return createdById;
    }

    public long getModifiedById() {
        return modifiedById;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setGeoJson(GeoJsonObject geoJson) {
        this.geoJson = geoJson;
    }

    public void setGeoJsonString(String geoJsonString) {
        this.geoJsonString = geoJsonString;
    }

    public void setGeoJsonFromString(){
        try {
            this.geoJson = new ObjectMapper().readValue(geoJsonString, GeoJsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(long minAltitude) {
        this.minAltitude = minAltitude;
    }


}
