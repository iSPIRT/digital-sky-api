package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.service.api.FlyDronePermissionApplicationService;
import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_adc_number")
public class AdcNumber {

  public static final char []adcPossibleChars = {'A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};

  @Autowired
  private FlyDronePermissionApplicationService flyDronePermissionApplicationService;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private long id;

  @Column(name = "FIRST_CHAR")
  private String type = "R";

  @Column(name = "SECOND_CHAR")
  private String fir;

  @Column(name = "THIRD_CHAR")
  private String alphabet;

  @Column(name = "FOURTH_FIFTH_SIXTH_NUM")
  private int fourthFifthSixthNumber;

  @Column(name= "SEVENTH_NUMBER")
  private int seventhNumber;

  @Column(name= "ASSIGNED_FLY_PERMISSION_ID")
  private String flyDronePermissionApplicationId;

  @Column(name = "CREATED_TIMESTAMP")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  @JsonIgnore
  private LocalDateTime createdTimestamp = LocalDateTime.now();

  @Column(name = "VALID_FOR_DATE")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  @JsonIgnore
  private LocalDate validForDate = LocalDate.now();


  public AdcNumber(String fir, String alphabet, int fourthFifthSixthNumber, int seventhNumber,String flyDronePermissionApplicationId) {
    this.fir = fir;
    this.alphabet = alphabet;
    this.fourthFifthSixthNumber = fourthFifthSixthNumber;
    this.seventhNumber = seventhNumber;
    this.flyDronePermissionApplicationId = flyDronePermissionApplicationId;
    FlyDronePermissionApplication application = flyDronePermissionApplicationService.get(flyDronePermissionApplicationId);
    if(application==null)
      throw new RuntimeException("No such flyDroneApplication Exists");
    this.validForDate= LocalDate.of(application.getStartDateTime().getYear(),application.getStartDateTime().getMonthValue(),application.getStartDateTime().getDayOfMonth());
  }

  public String getFir() {
    return fir;
  }

  public void setFir(String fir) {
    this.fir = fir;
  }

  public String getAlphabet() {
    return alphabet;
  }

  public void setAlphabet(String alphabet) {
    this.alphabet = alphabet;
  }

  public int getFourthFifthSixthNumber() {
    return fourthFifthSixthNumber;
  }

  public void setFourthFifthSixthNumber(int fourthFifthSixthNumber) {
    this.fourthFifthSixthNumber = fourthFifthSixthNumber;
  }

  public int getSeventhNumber() {
    return seventhNumber;
  }

  public void setSeventhNumber(int seventhNumber) {
    this.seventhNumber = seventhNumber;
  }

  public String getAdcNumber(){
    return this.type+fir+alphabet+fourthFifthSixthNumber+seventhNumber;
  }

  public void incrementSeventhNumber(){
    int newNumber = this.seventhNumber+1;
    if(seventhNumber%10==0){
      seventhNumber=1;
      incrementFourthFifthSixthNumber();
    }
    else{
      seventhNumber=newNumber;
    }
  }

  private void incrementFourthFifthSixthNumber() {
    int newNumber = this.fourthFifthSixthNumber+1;
    if(newNumber>999){
      fourthFifthSixthNumber=0;
      incrementAlphabet();
    }
    else{
      fourthFifthSixthNumber=newNumber;
    }
  }

  private void incrementAlphabet() {
    int newCharIndex = new String(adcPossibleChars).indexOf(alphabet) + 1;
    if(newCharIndex==adcPossibleChars.length){
      throw new RuntimeException("Index out of bounds for ADC");
    }
    else{
      alphabet = Character.toString(adcPossibleChars[newCharIndex]);
    }
  }

}
//select * from ds_user where ID=(select MAX(ID) from ds_user);