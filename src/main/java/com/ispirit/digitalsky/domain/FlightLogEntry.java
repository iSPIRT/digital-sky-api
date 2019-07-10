package com.ispirit.digitalsky.domain;

import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_flight_log")
public class FlightLogEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  public FlightLogEntry(String flyDroneApplicationId, String uin, String signature) {
    this.flyDroneApplicationId = flyDroneApplicationId;
    this.uin = uin;
    this.signature = signature;
  }

  @Column(name = "FLY_DRONE_APPLICATION_ID")
  @NotNull
  private String flyDroneApplicationId;

  @Column(name = "UIN_NO")
  private String uin;

  @Column(name = "SIGNATURE")
  private String signature;

  @Column(name = "CREATED_TIMESTAMP")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDateTime createdTimestamp = LocalDateTime.now();

  public String getUin() {
    return uin;
  }

  public String getSignature() {
    return signature;
  }
}
