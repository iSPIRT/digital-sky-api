package com.ispirit.digitalsky.domain;

import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.repository.FlyDronePermissionApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.FlyDronePermissionApplicationServiceImpl;
import com.ispirit.digitalsky.service.api.*;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class AdcNumberTest {

  private FlyDronePermissionApplicationRepository repository;
  private StorageService storageService;
  private FlyDronePermissionApplicationServiceImpl service;

  private AirspaceCategoryService airspaceCategoryService;
  private UserPrincipal userPrincipal;
  private DigitalSignService digitalSignService;
  private OperatorDroneService operatorDroneService;
  private UserProfileService userProfileService;
  private PilotService pilotService;
  private Configuration freemarkerConfiguration;
  private List<FlightInformationRegion> firs = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    repository = mock(FlyDronePermissionApplicationRepository.class);
    storageService = mock(StorageService.class);
    airspaceCategoryService = mock(AirspaceCategoryService.class);
    digitalSignService = mock(DigitalSignService.class);
    operatorDroneService = mock(OperatorDroneService.class);
    userProfileService = mock(UserProfileService.class);
    pilotService = mock(PilotService.class);
    freemarkerConfiguration = freemarkerConfiguration();
    File file = new File("chennaiFir.json");
    BufferedReader reader = new BufferedReader(new FileReader(file));
    firs.add(0, new FlightInformationRegion("Chennai", reader.readLine(), 'O'));
    file = new File("delhiFir.json");
    reader = new BufferedReader(new FileReader(file));
    firs.add(1, new FlightInformationRegion("Delhi",reader.readLine() , 'I'));
    file = new File("mumbaiFir.json");
    reader = new BufferedReader(new FileReader(file));
    firs.add(2, new FlightInformationRegion("Mumbai", reader.readLine(), 'A'));
    file = new File("kolkataFir.json");
    reader = new BufferedReader(new FileReader(file));
    firs.add(3, new FlightInformationRegion("Kolkata", reader.readLine(), 'E'));
    service = spy(new FlyDronePermissionApplicationServiceImpl(repository, storageService, airspaceCategoryService, digitalSignService, operatorDroneService, userProfileService, pilotService, freemarkerConfiguration,firs));
    userPrincipal = SecurityContextHelper.setUserSecurityContext();
  }

  @Test
  public void testEndWithZeroAdcNumber(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCA1230", LocalDate.of(2019,2,12),service);
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidFirRegionNumber(){
    try {
      AdcNumber testAdcNumber = new AdcNumber("RIA1231", LocalDate.of(2019,2,12),service);
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidAlphabet(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCI1111",LocalDate.of(2019,2,12),service);
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidAlphabetOther(){
    try{
      AdcNumber testAdcNumber1 = new AdcNumber("RCO1111",LocalDate.of(2019,2,12),service);
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch(RuntimeException e) {

    }
  }

  @Test
  public void testInvalidNumber(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCB1as1",LocalDate.of(2019,2,12),service);
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch (RuntimeException e){

    }
  }

  @Test
  public void testInvalidBeggingCharacter(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("KCB1111",LocalDate.of(2019,2,12),service);
      Assert.fail("Expected to fail as adc number isn't proper");
    }
    catch (RuntimeException e){

    }
  }

  @Test
  public void testSuccessfulCreationOfAdcNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0011",LocalDate.of(2019,2,12),service);
  }


  public freemarker.template.Configuration freemarkerConfiguration() {
    freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_20);
    cfg.setClassForTemplateLoading(this.getClass(), "/templates");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setLocalizedLookup(false);
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    return cfg;
  }

  @Test
  public void checkIfBothConstructorsCreateSame(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0011",LocalDate.of(2019,2,12),service);
    FlyDronePermissionApplication application = new FlyDronePermissionApplication();
    application.setStartDateTime(LocalDateTime.of(2019,2,12,11,10,1));
    when(service.get(anyString())).thenReturn(application);
    when(repository.findById(anyString())).thenReturn(application);
    AdcNumber testAdcNumberNew = new AdcNumber("C","A",1,1,"1",service);
    assertEquals(true,testAdcNumber.equals(testAdcNumberNew));
  }

  @Test
  public void testIncrementAtSeventhNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0011",LocalDate.of(2019,2,12),service);
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA0012",LocalDate.of(2019,2,12),service);
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtSixthNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0019",LocalDate.of(2019,2,12),service);
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA0021",LocalDate.of(2019,2,12),service);
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtFifthNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0099",LocalDate.of(2019,2,12),service);
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA0101",LocalDate.of(2019,2,12),service);
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtFourthNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA0999",LocalDate.of(2019,2,12),service);
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCA1001",LocalDate.of(2019,2,12),service);
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testIncrementAtThirdNumber(){
    AdcNumber testAdcNumber = new AdcNumber("RCA9999",LocalDate.of(2019,2,12),service);
    testAdcNumber.incrementSeventhNumber();
    AdcNumber expectedAdcNumber = new AdcNumber("RCB0001",LocalDate.of(2019,2,12),service);
    assertEquals(true,testAdcNumber.equals(expectedAdcNumber));
  }

  @Test
  public void testErrorWhenAllIdsDone(){
    try{
      AdcNumber testAdcNumber = new AdcNumber("RCZ9999",LocalDate.of(2019,2,12),service);
      testAdcNumber.incrementSeventhNumber();
      Assert.fail("Testing error on end of Ids");
    }
    catch (RuntimeException e){

    }
  }

  @Test
  public void testWhenNotEqual(){
    AdcNumber testAdcNumber = new AdcNumber("RCZ9999",LocalDate.of(2019,2,12),service);
    AdcNumber testAdcNumber1 = new AdcNumber("RCZ9993",LocalDate.of(2019,2,12),service);
    assertEquals(false,testAdcNumber.equals(testAdcNumber1));
  }

}
