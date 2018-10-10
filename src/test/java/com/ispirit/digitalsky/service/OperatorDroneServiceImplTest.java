package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.exception.ValidationException;
import com.ispirit.digitalsky.repository.OperatorDroneRepository;
import com.ispirit.digitalsky.service.api.UserProfileService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class OperatorDroneServiceImplTest {

    private OperatorDroneRepository operatorDroneRepository;
    private UserProfileService userProfileService;
    private OperatorDroneServiceImpl operatorDroneService;
    private UserPrincipal userPrincipal;

    @Before
    public void setUp() {
        operatorDroneRepository = mock(OperatorDroneRepository.class);
        userProfileService = mock(UserProfileService.class);
        userPrincipal = SecurityContextHelper.setUserSecurityContext();

        operatorDroneService = new OperatorDroneServiceImpl(operatorDroneRepository, userProfileService);
    }

    @Test
    public void shouldCreateOperatorDrones() {
        ArrayList<OperatorDrone> addedDrones = new ArrayList<>();
        OperatorDrone opDrone =  new OperatorDrone(1L, ApplicantType.INDIVIDUAL,"ef898888888",false);
        (addedDrones).add(opDrone);

        //given
        doReturn(addedDrones).when(operatorDroneRepository).save(anyListOf(OperatorDrone.class));

        //when
        List<OperatorDrone> drones =  operatorDroneService.createOperatorDrones(new ArrayList<>());

        //then
        verify(operatorDroneRepository).save(anyListOf(OperatorDrone.class));
        assertEquals(drones.size(), 1);
        assertEquals(drones.get(0).getAcquisitionApplicationId(), "ef898888888");
    }

    @Test
    public void shouldUpdateUINApplicationId() {

        OperatorDrone drone = new OperatorDrone(1L, ApplicantType.INDIVIDUAL, "ef898888888", false);

        //given
        doReturn(drone).when(operatorDroneRepository).findOne(2L);
        when(operatorDroneRepository.save(any(OperatorDrone.class))).thenReturn(any((OperatorDrone.class)));

        //when
        operatorDroneService.updateUINApplicationId(2L, "67eeeeeffff", OperatorDroneStatus.UIN_SUBMITTED);

        //then
        verify(operatorDroneRepository).findOne(2L);

        ArgumentCaptor<OperatorDrone> argumentCaptor = ArgumentCaptor.forClass(OperatorDrone.class);
        verify(operatorDroneRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getUinApplicationId(), is("67eeeeeffff"));
        assertThat(argumentCaptor.getValue().getOperatorDroneStatus(), is(OperatorDroneStatus.UIN_SUBMITTED));
    }

    @Test
    public void shouldFindOperatorDrone() {

        //given
        when(operatorDroneRepository.findOne(2L)).thenReturn(any(OperatorDrone.class));

        //when
        operatorDroneService.find(2L);

        //then
        verify(operatorDroneRepository).findOne(2L);
    }

    @Test
    public void shouldLoadByIndividualOperator() {
        UserProfile user =new UserProfile(1L,0,2L,0, 0, "", "", "");

        //given
        when(userProfileService.profile(eq(1L))).thenReturn(user);
        when(operatorDroneRepository.loadByOperator(eq(1L),any(ApplicantType.class))).thenReturn(anyListOf(OperatorDrone.class));

        //when
        List<?> opDrone = operatorDroneService.loadByOperator();

        //then
        verify(userProfileService).profile(1L);
        verify(operatorDroneRepository).loadByOperator(2L, ApplicantType.INDIVIDUAL);

        assert(opDrone != null);
    }

    @Test
    public void shouldLoadByOrganizationOperator() {
        UserProfile user =new UserProfile(1L,0,0,2L, 0, "", "", "");

        //given
        when(userProfileService.profile(eq(1L))).thenReturn(user);
        when(operatorDroneRepository.loadByOperator(eq(1L),any(ApplicantType.class))).thenReturn(anyListOf(OperatorDrone.class));

        //when
        List<?> opDrone = operatorDroneService.loadByOperator();

        //then
        verify(userProfileService).profile(1L);
        verify(operatorDroneRepository).loadByOperator(2L, ApplicantType.ORGANISATION);

        assert(opDrone != null);
    }

    @Test
    public void shouldThrowValidationExceptionIfOperatorNotFound() {

        UserProfile user =new UserProfile(1L,0,0,0, 1L, "", "", "");

        //given
        when(userProfileService.profile(eq(1L))).thenReturn(user);
        when(operatorDroneRepository.loadByOperator(eq(1L),any(ApplicantType.class))).thenReturn(anyListOf(OperatorDrone.class));

        //when
        try {
            List<?> opDrone = operatorDroneService.loadByOperator();
            fail("should throw ValidationException");
        } catch(ValidationException e) {

        }

        //then
        verify(userProfileService).profile(1L);
        verifyZeroInteractions(operatorDroneRepository);
    }

    @Test
    public void shouldUpdateStatus() {
        OperatorDrone drone = new OperatorDrone(1L, ApplicantType.INDIVIDUAL, "ef898888888", false);
        ArgumentCaptor<OperatorDrone> argumentCaptor = ArgumentCaptor.forClass(OperatorDrone.class);

        //given
        doReturn(drone).when(operatorDroneRepository).findOne(eq(2L));
        when(operatorDroneRepository.save(drone)).thenReturn(any(OperatorDrone.class));

        //when
        operatorDroneService.updateStatus(2L, OperatorDroneStatus.UIN_APPROVED);

        //then
        verify(operatorDroneRepository).findOne(2L);
        verify(operatorDroneRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getRegisteredDate(), is(LocalDate.now()));
        assertThat(argumentCaptor.getValue().getOperatorDroneStatus(), is(OperatorDroneStatus.UIN_APPROVED));
    }

    @Test
    public void shouldNotSetRegisteredDateIfNotApprovedWhileUpdatingStatus() {
        OperatorDrone drone = new OperatorDrone(1L, ApplicantType.INDIVIDUAL, "ef898888888", false);
        ArgumentCaptor<OperatorDrone> argumentCaptor = ArgumentCaptor.forClass(OperatorDrone.class);

        //given
        doReturn(drone).when(operatorDroneRepository).findOne(eq(2L));
        when(operatorDroneRepository.save(drone)).thenReturn(any(OperatorDrone.class));

        //when
        operatorDroneService.updateStatus(2L, OperatorDroneStatus.UIN_REJECTED);

        //then
        verify(operatorDroneRepository).findOne(2L);
        verify(operatorDroneRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getRegisteredDate(), is(nullValue()) );
        assertThat(argumentCaptor.getValue().getOperatorDroneStatus(), is(OperatorDroneStatus.UIN_REJECTED));
    }

    @Test
    public void shouldUpdateUniqueDeviceId() {

        ArgumentCaptor<OperatorDrone> argumentCaptor = ArgumentCaptor.forClass(OperatorDrone.class);

        //given
        OperatorDrone drone = new OperatorDrone(1L, ApplicantType.INDIVIDUAL, "ef898888888", false);
        doReturn(drone).when(operatorDroneRepository).findOne(eq(2L));
        when(operatorDroneRepository.save(drone)).thenReturn(mock(OperatorDrone.class));

        //when
        OperatorDrone opDrone = operatorDroneService.updateUniqueDeviceId(2L, "56efff543");

        //then
        verify(operatorDroneRepository).findOne(2L);
        verify(operatorDroneRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getDeviceId(), is("56efff543"));

        assert(opDrone != null);
    }

    @Test
    public void shouldGetAvailableDroneDeviceIds() {

        UserProfile user =new UserProfile(1L,0,2L,0, 0, "", "", "");
        List<OperatorDrone> operatorDrones = new ArrayList<OperatorDrone>();
        OperatorDrone opDroneA = new OperatorDrone(2L, ApplicantType.INDIVIDUAL, "eff234", false);
        OperatorDrone opDroneB = new OperatorDrone(2L, ApplicantType.INDIVIDUAL, "eff233", false);

        opDroneA.setDeviceId("Beebop1.0");
        opDroneB.setDeviceId("Beebop2.0");
        operatorDrones.addAll(asList(opDroneA, opDroneB));

        //given
        when(userProfileService.profile(eq(1L))).thenReturn(user);
        when(operatorDroneRepository.loadByOperator(eq(2L),eq(ApplicantType.INDIVIDUAL))).thenReturn(operatorDrones);

        //when
        Collection<?> ids = operatorDroneService.getAvailableDroneDeviceIds(asList("Beebop3.0", "Beebop4.0"));

        //then
        verify(userProfileService).profile(1L);
        verify(operatorDroneRepository).loadByOperator(2L, ApplicantType.INDIVIDUAL);
        assertThat(ids.size(), is(2));
        assert(ids.contains("Beebop3.0"));
        assert(ids.contains("Beebop4.0"));

    }

    @Test
    public void shouldNotReturnUsedDeviceIdsWhileGettingAvailableDroneDeviceIds() {
        UserProfile user =new UserProfile(1L,0,2L,0, 0, "", "", "");
        List<OperatorDrone> operatorDrones = new ArrayList<OperatorDrone>();
        OperatorDrone opDroneA = new OperatorDrone(2L, ApplicantType.INDIVIDUAL, "eff234", false);
        OperatorDrone opDroneB = new OperatorDrone(2L, ApplicantType.INDIVIDUAL, "eff233", false);

        opDroneA.setDeviceId("Beebop1.0");
        opDroneB.setDeviceId("Beebop2.0");
        operatorDrones.addAll(asList(opDroneA, opDroneB));

        //given
        when(userProfileService.profile(eq(1L))).thenReturn(user);
        when(operatorDroneRepository.loadByOperator(eq(2L),eq(ApplicantType.INDIVIDUAL))).thenReturn(operatorDrones);

        //when
        Collection<?> ids = operatorDroneService.getAvailableDroneDeviceIds(asList("Beebop1.0", "Beebop4.0"));

        //then
        verify(userProfileService).profile(1L);
        verify(operatorDroneRepository).loadByOperator(2L, ApplicantType.INDIVIDUAL);
        assertThat(ids.size(), is(1));
        assert(ids.contains("Beebop4.0"));
    }

    @Test
    public void shouldReturnFalseForDeviceIdMappedToADifferentUIN() {

        List<OperatorDrone> operatorDrones = new ArrayList<OperatorDrone>();
        OperatorDrone opDroneA = new OperatorDrone(2L, ApplicantType.ORGANISATION, "eff234", false);
        OperatorDrone opDroneB = new OperatorDrone(2L, ApplicantType.ORGANISATION, "eff233", false);

        opDroneA.setDeviceId("Beebop1.0");
        opDroneA.setUinApplicationId("aa234eef78543");
        opDroneB.setDeviceId("Beebop2.0");
        opDroneB.setUinApplicationId("bb234eef78543");
        operatorDrones.addAll(asList(opDroneA, opDroneB));

        //given
        when(operatorDroneRepository.loadByOperator(eq(2L), eq(ApplicantType.ORGANISATION))).thenReturn(operatorDrones);

        //when
        boolean isMapped = operatorDroneService.isMappedToDifferentUIN("Beebop3.0", "234eef78543", 2L, ApplicantType.ORGANISATION);

        //then
        verify(operatorDroneRepository).loadByOperator(2L, ApplicantType.ORGANISATION);
        assert(isMapped == false);
    }

    @Test
    public void shouldReturnTrueForDeviceIdMappedToADifferentUIN() {
        List<OperatorDrone> operatorDrones = new ArrayList<OperatorDrone>();
        OperatorDrone opDroneA = new OperatorDrone(2L, ApplicantType.ORGANISATION, "eff234", false);
        OperatorDrone opDroneB = new OperatorDrone(2L, ApplicantType.ORGANISATION, "eff233", false);

        opDroneA.setDeviceId("Beebop1.0");
        opDroneA.setUinApplicationId("aa234eef78543");
        opDroneB.setDeviceId("Beebop3.0");
        opDroneB.setUinApplicationId("bb234eef78543");
        operatorDrones.addAll(asList(opDroneA, opDroneB));

        //given
        when(operatorDroneRepository.loadByOperator(eq(2L), eq(ApplicantType.ORGANISATION))).thenReturn(operatorDrones);

        //when
        boolean isMapped = operatorDroneService.isMappedToDifferentUIN("Beebop3.0", "234eef78543", 2L, ApplicantType.ORGANISATION);

        //then
        verify(operatorDroneRepository).loadByOperator(2L, ApplicantType.ORGANISATION);
        assert(isMapped);
    }

    @Test
    public void shouldReturnFalseForTheSameOpDrone() {
        List<OperatorDrone> operatorDrones = new ArrayList<OperatorDrone>();
        OperatorDrone opDroneB = new OperatorDrone(2L, ApplicantType.ORGANISATION, "eff233", false);

        opDroneB.setDeviceId("Beebop3.0");
        opDroneB.setUinApplicationId("234eef78543");
        operatorDrones.addAll(asList(opDroneB));

        //given
        when(operatorDroneRepository.loadByOperator(eq(2L), eq(ApplicantType.ORGANISATION))).thenReturn(operatorDrones);

        //when
        boolean isMapped = operatorDroneService.isMappedToDifferentUIN("Beebop3.0", "234eef78543", 2L, ApplicantType.ORGANISATION);

        //then
        verify(operatorDroneRepository).loadByOperator(2L, ApplicantType.ORGANISATION);
        assert(isMapped == false);
    }

}
