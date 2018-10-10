package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.exception.PilotProfileAlreadyExist;
import com.ispirit.digitalsky.repository.PilotRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class PilotServiceImplTest {

    private StorageService storageService;
    private PilotRepository repository;
    private PilotServiceImpl service;

    @Before
    public void setUp() throws Exception {
        storageService = mock(StorageService.class);
        repository = mock(PilotRepository.class);
        service = new PilotServiceImpl(repository, storageService);
    }

    @Test
    public void shouldCheckForExistingProfileBeforeCreating() throws Exception {
        //given
        Pilot pilot = new Pilot(1);
        pilot.setResourceOwnerId(2L);
        when(repository.loadByResourceOwner(pilot.getResourceOwnerId())).thenReturn(pilot);

        //when
        try {
            service.createNewPilot(pilot);
            fail("should have thrown PilotProfileAlreadyExist");
        } catch (PilotProfileAlreadyExist e) {
        }
    }

    @Test
    public void shouldCreateProfile() throws Exception {
        //given
        Pilot pilot = new Pilot(1);
        pilot.setResourceOwnerId(2L);
        when(repository.loadByResourceOwner(pilot.getResourceOwnerId())).thenReturn(null);

        //when
        service.createNewPilot(pilot);

        //then
        verify(repository).save(pilot);
    }

    @Test
    public void shouldSaveTrainingCertificateWhenCreatingProfile() throws Exception {
        //given
        Pilot pilot = new Pilot(1);
        pilot.setResourceOwnerId(2L);
        MockMultipartFile trainingCertificate = new MockMultipartFile("trainingCertificate", "content".getBytes());
        pilot.setTrainingCertificate(trainingCertificate);
        when(repository.loadByResourceOwner(pilot.getResourceOwnerId())).thenReturn(null);

        //when
        service.createNewPilot(pilot);

        //then
        verify(storageService).store(Arrays.asList(trainingCertificate),"Pilot-1");
    }

    @Test
    public void shouldUpdateProfile() throws Exception {
        //given
        Pilot pilot = new Pilot(2L,"","name","email","mobile","country",null,null, Collections.emptyList());

        //when
        service.updatePilot(1L, pilot);

        //then
        ArgumentCaptor<Pilot> argumentCaptor = ArgumentCaptor.forClass(Pilot.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId(), is(1L));
    }

    @Test
    public void shouldSaveTrainingCertificateDuringUpdateProfile() throws Exception {
        //given
        MockMultipartFile trainingCertificate = new MockMultipartFile("trainingCertificate", "content".getBytes());
        Pilot pilot = new Pilot(2L,"","name","email","mobile","country",null,null, Collections.emptyList());
        pilot.setTrainingCertificate(trainingCertificate);
        //when
        service.updatePilot(1L, pilot);

        //then
        verify(storageService).store(Arrays.asList(trainingCertificate),"Pilot-1");
    }

    @Test
    public void shouldFindPilotById() throws Exception {
        //given
        long id = 1L;
        Pilot pilot = new Pilot(id);
        when(repository.findOne(id)).thenReturn(pilot);

        //when
        Pilot result = service.find(id);

        //then
        verify(repository).findOne(id);
        assertThat(result, is(pilot));
    }

    @Test
    public void shouldFindPilotBusinessIdentifier() throws Exception {
        //given
        String businessIdentifier="test";
        Pilot pilot = new Pilot(1L);
        when(repository.loadByBusinessIdentifier(businessIdentifier)).thenReturn(pilot);

        //when
        Pilot result = service.findByBusinessIdentifier(businessIdentifier);

        //then
        verify(repository).loadByBusinessIdentifier(businessIdentifier);
        assertThat(result, is(pilot));
    }

    @Test
    public void shoulGetTrainingCertificate() throws Exception {
        //given
        Pilot pilot = new Pilot(1L);
        pilot.setTrainingCertificateDocName("name");

        //when
        service.trainingCertificate(pilot);

        //then
        verify(storageService).loadAsResource("Pilot-1", pilot.getTrainingCertificateDocName());
    }
}