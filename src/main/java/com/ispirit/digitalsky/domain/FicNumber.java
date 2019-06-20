package com.ispirit.digitalsky.domain;

import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "ds_fic_number")
public class FicNumber {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private static final String TYPE = "R";

  @Column(name = "FIRST_TO_FIFTH_NUMBERS")
  private int firstToFifthNumbers;

  @Column(name = "LAST_CHAR")
  @NotNull
  @Size(max = 1)
  private String lastChar;

  @Column(name = "CREATED_TIMESTAMP")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDateTime createdTimestamp = LocalDateTime.now();

  @Column(name = "VALID_FOR_DATE")
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  private LocalDateTime validForDate;

  @Column(name = "FIC_NUMBER")
  @Size(max = 7)
  private String ficNumber;

  @Size(max = 30)
  @Column(name= "ASSIGNED_FLY_PERMISSION_ID")
  private String flyDronePermissionApplicationId;

  private enum firToAlphabet{
    MUMBAI("A"),KOLKATA("E"),DELHI("I"),CHENNAI("O");

    private String value;

    firToAlphabet(String value){
      this.value=value;
    }

    String getValue(){return value;}

    static String getFromFir(String fir){
      switch (fir){
        case "C":return CHENNAI.getValue();
        case "D":return DELHI.getValue();
        case "M":return MUMBAI.getValue();
        case "K":return KOLKATA.getValue();
        default: throw new RuntimeException("Unidentified FIR code");
      }
    }

  }

  private FicNumber(){

  }

  public FicNumber(String fir,int firstToFifthNumbers, String flyDronePermissionApplicationId,LocalDateTime validForDate) {
    this.lastChar = firToAlphabet.getFromFir(fir);
    this.firstToFifthNumbers = firstToFifthNumbers;
    this.flyDronePermissionApplicationId = flyDronePermissionApplicationId;
    this.validForDate= validForDate;
    setFicNumber(String.format("%05d", firstToFifthNumbers)+this.TYPE+lastChar);
  }

  public FicNumber(String ficNumber, LocalDateTime validForDate){
    if(ficNumber.length()!=7)
      throw new RuntimeException("Length of fic number is not 7");
    if(ficNumber.charAt(5)!='R')
      throw new RuntimeException("FIC number doesn't have R at position 6");
    this.lastChar = Character.toString(ficNumber.charAt(6));
    domainCheck(lastChar);
    try {
      this.firstToFifthNumbers = Integer.parseInt(ficNumber.substring(0,5));
    }
    catch (NumberFormatException e){
      throw new RuntimeException("Not a valid FIC Number as the first 5 characters do not represent a number");
    }
    this.validForDate = validForDate;
    setFicNumber(String.format("%05d", firstToFifthNumbers)+this.TYPE+lastChar);
  }

  private void domainCheck(String lastChar) {
    switch (lastChar){
      case "A":
      case "E":
      case "I":
      case "O":return;
      default: throw new RuntimeException("Not a valid FIR representative character it needs to be A,E,I,O only");
    }
  }

  public String getFicNumber() {
    return ficNumber;
  }

  public void setFicNumber(String ficNumber) {
    this.ficNumber = ficNumber;
  }

  public void incrementNum(){
    int newNumber = firstToFifthNumbers + 1;
    if(newNumber>99999){
      throw new RuntimeException("Index out of bounds for FIC number");
    }
    else{
      firstToFifthNumbers = newNumber;
    }
    setFicNumber(String.format("%05d", firstToFifthNumbers)+this.TYPE+lastChar);
  }

}
