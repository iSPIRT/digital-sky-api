package com.ispirit.digitalsky.domain;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;


public class AdcNumberTest {


  @Test
  public void testEndWithZeroAdcNumber(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCA1230", LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidFirRegionNumber(){
    try {
      AdcNumber testAdcNumber = new AdcNumber("RIA1231", LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidAlphabet(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCI1111",LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidAlphabetOther(){
    try{
      AdcNumber testAdcNumber1 = new AdcNumber("RCO1111",LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidNumber(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCB1as1",LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch (RuntimeException e){

    }
  }

  @Test
  public void testInvalidBeggingCharacter(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("KCB1111",LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch (RuntimeException e){

    }
  }

  @Test
  public void testSuccessfulCreationOfAdcNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0011",LocalDateTime.of(2019,2,12,0,0));
  }

  @Test
  public void testIncrementAtSeventhNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0011",LocalDateTime.of(2019,2,12,0,0));
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA0012",LocalDateTime.of(2019,2,12,0,0));
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtSixthNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0019",LocalDateTime.of(2019,2,12,0,0));
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA0021",LocalDateTime.of(2019,2,12,0,0));
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtFifthNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0099",LocalDateTime.of(2019,2,12,0,0));
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA0101",LocalDateTime.of(2019,2,12,0,0));
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtFourthNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0999",LocalDateTime.of(2019,2,12,0,0));
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA1001",LocalDateTime.of(2019,2,12,0,0));
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtThirdNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA9999",LocalDateTime.of(2019,2,12,0,0));
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCB0001",LocalDateTime.of(2019,2,12,0,0));
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testErrorWhenAllIdsDone(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCZ9999",LocalDateTime.of(2019,2,12,0,0));
      testAdcNumber.incrementSeventhNumber();
      Assert.fail("Testing error on end of Ids");
    }
    catch (RuntimeException e){

    }
  }

  @Test
  public void testWhenNotEqual(){
    AdcNumber testAdcNumber = new AdcNumber("RCZ9999",LocalDateTime.of(2019,2,12,0,0));
    AdcNumber testAdcNumber1 = new AdcNumber("RCZ9993",LocalDateTime.of(2019,2,12,0,0));
    assertEquals(false,testAdcNumber.equals(testAdcNumber1));
  }

}
