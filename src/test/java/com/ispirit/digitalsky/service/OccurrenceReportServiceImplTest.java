package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.OccurrenceReport;
import com.ispirit.digitalsky.repository.OccurrenceReportRepository;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OccurrenceReportServiceImplTest {

    private OccurrenceReportRepository repository;
    private OccurrenceReportServiceImpl service;

    @Before
    public void setUp() throws Exception {
        repository = mock(OccurrenceReportRepository.class);
        service = new OccurrenceReportServiceImpl(repository);
    }

    @Test
    public void shouldCreateReport() throws Exception {
        //given
        OccurrenceReport occurrenceReport = new OccurrenceReport();

        //when
        service.createNew(occurrenceReport);

        //then
        verify(repository).save(occurrenceReport);
    }

    @Test
    public void shouldFindReportById() throws Exception {
        //given
        long id = 1L;
        OccurrenceReport occurrenceReport = new OccurrenceReport();
        when(repository.findOne(id)).thenReturn(occurrenceReport);

        //when
        OccurrenceReport result = service.find(id);

        //then
        verify(repository).findOne(id);
        assertThat(result, is(occurrenceReport));
    }

    @Test
    public void shouldFindReportsByDroneId() throws Exception {
        //given
        long droneId = 1L;
        OccurrenceReport occurrenceReportOne = new OccurrenceReport();
        OccurrenceReport occurrenceReportTwo = new OccurrenceReport();
        when(repository.findByDroneId(droneId)).thenReturn(asList(occurrenceReportOne, occurrenceReportTwo));

        //when
        List<OccurrenceReport> result = service.findByDroneId(droneId);

        //then
        assertThat(result, is(asList(occurrenceReportOne, occurrenceReportTwo)));
    }
}