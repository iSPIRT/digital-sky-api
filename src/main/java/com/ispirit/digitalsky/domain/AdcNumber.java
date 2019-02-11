package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.service.api.FlyDronePermissionApplicationService;
import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_adc_number")
public class AdcNumber {

  public static final char []adcPossibleChars = {'A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};

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
  private LocalDateTime createdTimestamp = LocalDateTime.now();

  @Column(name = "VALID_FOR_DATE")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDate validForDate = LocalDate.now();

  @Column(name = "ADC_NUMBER")
  private String adcNumber;


  public AdcNumber(String fir, String alphabet, int fourthFifthSixthNumber, int seventhNumber,String flyDronePermissionApplicationId,FlyDronePermissionApplicationService flyDronePermissionApplicationService) {
    checkIfDomainValid(alphabet,seventhNumber,fir);
    this.fir = fir;
    this.alphabet = alphabet.toUpperCase();
    this.fourthFifthSixthNumber = fourthFifthSixthNumber;
    this.seventhNumber = seventhNumber;
    this.flyDronePermissionApplicationId = flyDronePermissionApplicationId;
    FlyDronePermissionApplication application = flyDronePermissionApplicationService.get(flyDronePermissionApplicationId);
    if(application==null)
      throw new RuntimeException("No such flyDroneApplication Exists");
    this.validForDate= LocalDate.of(application.getStartDateTime().getYear(),application.getStartDateTime().getMonthValue(),application.getStartDateTime().getDayOfMonth());
    setAdcNumber(this.type+fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
    this.flyDronePermissionApplicationService = flyDronePermissionApplicationService;
  }

  private void checkIfDomainValid(String alphabet, int seventhNumber,String fir) {
    if(alphabet.equals("I")|| alphabet.equals("O"))
      throw new RuntimeException("Not a valid ADC Number because the ADC Number contains I/O");
    if(seventhNumber==0)
      throw new RuntimeException("Not a valid ADC Number because the ADC Number ends with 0");
    if(! (fir.equals("C") || fir.equals("K") || fir.equals("M") || fir.equals("D")))
      throw new RuntimeException("Not a valid FIR");
  }

  public AdcNumber(String adcNumber, LocalDate validForDate , FlyDronePermissionApplicationService flyDronePermissionApplicationService){
    if(adcNumber.length()!=7)
      throw new RuntimeException("Length of ADC number is not 7");
    if(adcNumber.charAt(0)!='R')
      throw new RuntimeException("ADC number starts with something that is not R");
    this.fir = Character.toString(adcNumber.charAt(1));
    this.alphabet = Character.toString(adcNumber.charAt(2)).toUpperCase();
    try {
      this.fourthFifthSixthNumber = Integer.parseInt(adcNumber.substring(3, 6));
      this.seventhNumber = Integer.parseInt(Character.toString(adcNumber.charAt(6)));
    }
    catch (NumberFormatException e){
      throw new RuntimeException("Not a valid ADC Number as the characters in 4th, 5th and 6th position do not represent a number");
    }
    checkIfDomainValid(this.alphabet,this.seventhNumber,this.fir);
    this.validForDate = validForDate;
    this.flyDronePermissionApplicationService = flyDronePermissionApplicationService;
    setAdcNumber(this.type+fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
  }

//  public String getFir() {
//    return fir;
//  }

//  public void setFir(String fir) {
//    this.fir = fir;
//  }

//  public String getAlphabet() {
//    return alphabet;
//  }

//  public void setAlphabet(String alphabet) {
//    this.alphabet = alphabet;
//  }

//  public int getFourthFifthSixthNumber() {
//    return fourthFifthSixthNumber;
//  }

//  public void setFourthFifthSixthNumber(int fourthFifthSixthNumber) {
//    this.fourthFifthSixthNumber = fourthFifthSixthNumber;
//  }

//  public int getSeventhNumber() {
//    return seventhNumber;
//  }

//  public void setSeventhNumber(int seventhNumber) {
//    this.seventhNumber = seventhNumber;
//  }

  public String getAdcNumber(){
    return this.adcNumber;
  }

  public void incrementSeventhNumber(){
    int newNumber = this.seventhNumber+1;
    if(newNumber%10==0){
      seventhNumber=1;
      incrementFourthFifthSixthNumber();
    }
    else{
      seventhNumber=newNumber;
    }
    setAdcNumber(this.type+fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
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
    setAdcNumber(this.type+fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
  }

  private void incrementAlphabet() {
    int newCharIndex = new String(adcPossibleChars).indexOf(alphabet) + 1;
    if(newCharIndex==adcPossibleChars.length){
      throw new RuntimeException("Index out of bounds for ADC");
    }
    else{
      alphabet = Character.toString(adcPossibleChars[newCharIndex]);
    }
    setAdcNumber(this.type+fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
  }

  private void setAdcNumber(String adcNumber) {
    this.adcNumber = adcNumber;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!AdcNumber.class.isAssignableFrom(obj.getClass())) {
      return false;
    }

    final AdcNumber other = (AdcNumber) obj;
    if (this.type != other.type || !this.fir.equals(other.fir) || !this.alphabet.equals(other.alphabet)  || this.fourthFifthSixthNumber != other.fourthFifthSixthNumber || this.seventhNumber!=other.seventhNumber || !this.validForDate.isEqual(other.validForDate) || !this.adcNumber.equals(other.adcNumber)) {
      return false;
    }

    return true;
  }
}