package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.AdcNumber;
import com.ispirit.digitalsky.repository.AdcNumberRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdcNumberServiceImplTest {

  private AdcNumberRepository adcNumberRepository;

  private AdcNumberServiceImpl service;

  @Before
  public void setUp() {
    adcNumberRepository = mock(AdcNumberRepository.class);
     service = new AdcNumberServiceImpl(adcNumberRepository);
  }

  @Test
  public void generateNewAdcWhenNoNumbersForDay(){
    when(adcNumberRepository.findNumberOfEntriesForDate(LocalDateTime.of(2019,2,12,0,0))).thenReturn(0l);
    FlyDronePermissionApplication application = new FlyDronePermissionApplication();
    application.setFir("Chennai");
    application.setStartDateTime(LocalDateTime.of(2019,2,12,0,0));
    application.setId("abc");
    AdcNumber adcNumberExpectedToGetCreated = new AdcNumber(Character.toString(application.getFir().charAt(0)).toUpperCase(), "A", 0, 1, "abc", LocalDateTime.of(2019,2,12,0,0));
    when(adcNumberRepository.save(any(AdcNumber.class))).thenReturn(adcNumberExpectedToGetCreated);

    String adcNumber = service.generateNewAdcNumber(application);
    assertEquals(true,adcNumber.equals("RCA0001"));
  }

  @Test
  public void generateNewAdcWhenAdcNumberExists(){
    when(adcNumberRepository.findNumberOfEntriesForDate(LocalDateTime.of(2019,2,12,0,0))).thenReturn(1l);
    FlyDronePermissionApplication application = new FlyDronePermissionApplication();
    application.setFir("Chennai");
    application.setStartDateTime(LocalDateTime.of(2019,2,12,0,0));
    application.setId("abc");
    when(adcNumberRepository.loadLatestAdcNumberOfDate(LocalDateTime.of(2019,2,12,0,0))).thenReturn(1l);
    AdcNumber adcNumberFromDb = new AdcNumber(Character.toString(application.getFir().charAt(0)).toUpperCase(), "A", 0, 1, "abc", LocalDateTime.of(2019,2,12,0,0));
    when(adcNumberRepository.findOne(1l)).thenReturn(adcNumberFromDb);
    when(adcNumberRepository.save(any(AdcNumber.class))).thenReturn(adcNumberFromDb);

    String adcNumber = service.generateNewAdcNumber(application);
    assertEquals(true,adcNumber.equals("RCA0002"));
  }

}
