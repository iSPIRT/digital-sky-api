package com.ispirit.digitalsky.document;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Document(collection = "flyDronePermissionApplications")
@TypeAlias("flyDronePermissionApplications")
public class FlyDronePermissionApplication extends BasicApplication {

    @Field("name")
    @NotNull
    private String pilotId;

    @Field("flyArea")
    @NotNull
    private List<LatLong> flyArea;

    @Field("droneId")
    @NotNull
    private long droneId;

    public FlyDronePermissionApplication() {
        setCreatedDate(new Date());
        setLastModifiedDate(new Date());
    }

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
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
}
