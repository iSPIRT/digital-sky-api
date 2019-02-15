package com.ispirit.digitalsky.domain;

import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_adc_number")
public class AdcNumber {

  public static final char []adcPossibleChars = {'A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private static final String TYPE = "R";

  @Column(name = "SECOND_CHAR")
  @NotNull
  @Size(max = 1)
  private String fir;

  @Column(name = "THIRD_CHAR")
  @NotNull
  @Size(max = 1)
  private String alphabet;

  @Column(name = "FOURTH_FIFTH_SIXTH_NUM")
  private int fourthFifthSixthNumber;

  @Column(name= "SEVENTH_NUMBER")
  private int seventhNumber;

  @Size(max = 30)
  @Column(name= "ASSIGNED_FLY_PERMISSION_ID")
  private String flyDronePermissionApplicationId;

  @Column(name = "CREATED_TIMESTAMP")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDateTime createdTimestamp = LocalDateTime.now();

  @Column(name = "VALID_FOR_DATE")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDateTime validForDate;

  @Column(name = "ADC_NUMBER")
  @Size(max = 7)
  private String adcNumber;


  public AdcNumber(String fir, String alphabet, int fourthFifthSixthNumber, int seventhNumber, String flyDronePermissionApplicationId,LocalDateTime validForDate) {
    checkIfDomainValid(alphabet,seventhNumber,fir);
    this.fir = fir;
    this.alphabet = alphabet.toUpperCase();
    this.fourthFifthSixthNumber = fourthFifthSixthNumber;
    this.seventhNumber = seventhNumber;
    this.flyDronePermissionApplicationId = flyDronePermissionApplicationId;
    this.validForDate= validForDate;
    setAdcNumber(this.TYPE +fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
  }

  private void checkIfDomainValid(String alphabet, int seventhNumber,String fir) {
    if(alphabet.equals("I")|| alphabet.equals("O"))
      throw new RuntimeException("Not a valid ADC Number because the ADC Number contains I/O");
    if(seventhNumber==0)
      throw new RuntimeException("Not a valid ADC Number because the ADC Number ends with 0");
    if(! (fir.equals("C") || fir.equals("K") || fir.equals("M") || fir.equals("D")))
      throw new RuntimeException("Not a valid FIR");
  }

  public AdcNumber(String adcNumber, LocalDateTime validForDate){
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
    setAdcNumber(this.TYPE +fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
  }

  private AdcNumber(){

  }

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
    setAdcNumber(this.TYPE +fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
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
    setAdcNumber(this.TYPE +fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
  }

  private void incrementAlphabet() {
    int newCharIndex = new String(adcPossibleChars).indexOf(alphabet) + 1;
    if(newCharIndex==adcPossibleChars.length){
      throw new RuntimeException("Index out of bounds for ADC");
    }
    else{
      alphabet = Character.toString(adcPossibleChars[newCharIndex]);
    }
    setAdcNumber(this.TYPE +fir+alphabet+String.format("%03d", fourthFifthSixthNumber)+seventhNumber);
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
    if (this.TYPE != other.TYPE || !this.fir.equals(other.fir) || !this.alphabet.equals(other.alphabet)  || this.fourthFifthSixthNumber != other.fourthFifthSixthNumber || this.seventhNumber!=other.seventhNumber || !this.validForDate.isEqual(other.validForDate) || !this.adcNumber.equals(other.adcNumber)) {
      return false;
    }

    return true;
  }
}