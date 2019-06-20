package com.ispirit.digitalsky.domain;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class FicNumberTest {

  @Test
  public void testForIndexOverflow(){
    try{
      FicNumber newFicNumber = new FicNumber("99999RA", LocalDateTime.of(2019,2,12,0,0));
      newFicNumber.incrementNum();
      Assert.fail("Expected to fail as fic number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testForInvalidFicNumber(){
    try{
      FicNumber newFicNumber = new FicNumber("99999CA", LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as fic number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testForInvalidFirInFicNumber(){
    try{
      FicNumber newFicNumber = new FicNumber("99999RV", LocalDateTime.of(2019,2,12,0,0));
      Assert.fail("Expected to fail as fic number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void equivalentConstructors(){
    FicNumber ficNumber = new FicNumber("00001RA",LocalDateTime.of(2019,2,12,0,0));
    FicNumber expectedFicNumber = new FicNumber("M",1,"",LocalDateTime.of(2019,2,12,0,0));
    assertEquals(true,ficNumber.getFicNumber().equals(expectedFicNumber.getFicNumber()));
  }

  @Test
  public void testForIncrement(){
    FicNumber expectedFicNumber = new FicNumber("00002RA",LocalDateTime.of(2019,2,12,0,0));
    FicNumber ficNumber = new FicNumber("M",1,"",LocalDateTime.of(2019,2,12,0,0));
    ficNumber.incrementNum();
    assertEquals(true,ficNumber.getFicNumber().equals(expectedFicNumber.getFicNumber()));
  }

}
