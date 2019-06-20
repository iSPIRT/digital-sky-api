package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.domain.FicNumber;
import com.ispirit.digitalsky.repository.FicNumberRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FicNumberServiceImplTest {

  private FicNumberRepository ficNumberRepository;

  private FicNumberServiceImpl service;


  @Before
  public void setUp() {
    ficNumberRepository = mock(FicNumberRepository.class);
    service = new FicNumberServiceImpl(ficNumberRepository);
  }

  @Test
  public void generateNewFicWhenNoNumbersForDay(){
    when(ficNumberRepository.findNumberOfEntriesForDate(LocalDateTime.of(2019,2,12,0,0))).thenReturn(0l);
    FlyDronePermissionApplication application = new FlyDronePermissionApplication();
    application.setFir("Chennai");
    application.setStartDateTime(LocalDateTime.of(2019,2,12,0,0));
    application.setId("abc");
    FicNumber ficNumberExpectedToGetCreated = new FicNumber(Character.toString(application.getFir().charAt(0)).toUpperCase(),  0, "abc", LocalDateTime.of(2019,2,12,0,0));
    when(ficNumberRepository.save(any(FicNumber.class))).thenReturn(ficNumberExpectedToGetCreated);

    String ficNumber = service.generateNewFicNumber(application);
    assertEquals(true,ficNumber.equals("00000RO"));
  }

  @Test
  public void generateNewFicWhenFicNumberExists(){
    when(ficNumberRepository.findNumberOfEntriesForDate(LocalDateTime.of(2019,2,12,0,0))).thenReturn(1l);
    FlyDronePermissionApplication application = new FlyDronePermissionApplication();
    application.setFir("Chennai");
    application.setStartDateTime(LocalDateTime.of(2019,2,12,0,0));
    application.setId("abc");
    when(ficNumberRepository.loadLatestFicNumberOfDate(LocalDateTime.of(2019,2,12,0,0))).thenReturn(1l);
    FicNumber ficNumberFromDb = new FicNumber(Character.toString(application.getFir().charAt(0)).toUpperCase(),0,"abc",LocalDateTime.of(2019,2,12,0,0));
    when(ficNumberRepository.findOne(1l)).thenReturn(ficNumberFromDb);
    when(ficNumberRepository.save(any(FicNumber.class))).thenReturn(ficNumberFromDb);
    String ficNumber = service.generateNewFicNumber(application);
    assertEquals(true,ficNumber.equals("00001RO"));
  }


}
