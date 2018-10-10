package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.ManufacturerRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.repository.PilotRepository;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserProfileServiceImplTest {

    private PilotRepository pilotRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private ManufacturerRepository manufacturerRepository;
    private UserProfileServiceImpl service;

    @Before
    public void setUp() throws Exception {

        pilotRepository = mock(PilotRepository.class);
        individualOperatorRepository = mock(IndividualOperatorRepository.class);
        organizationOperatorRepository = mock(OrganizationOperatorRepository.class);
        manufacturerRepository = mock(ManufacturerRepository.class);

        service = new UserProfileServiceImpl(
                pilotRepository, individualOperatorRepository, organizationOperatorRepository, manufacturerRepository);
    }

    @Test
    public void shouldLoadUserIndividualOperatorProfileById() throws Exception {
        //given
        long id = 1L;
        Pilot pilot = new Pilot(1L);
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());
        Manufacturer manufacturer = new Manufacturer(1, "", "name", "email", "9898121212", "123455", "India", emptyList());

        when(pilotRepository.loadByResourceOwner(id)).thenReturn(pilot);
        when(individualOperatorRepository.loadByResourceOwner(id)).thenReturn(individualOperator);
        when(manufacturerRepository.loadByResourceOwner(id)).thenReturn(manufacturer);

        //when
        UserProfile userProfile = service.profile(id);

        assertThat(userProfile.getPilotProfileId(), is(pilot.getId()));
        assertThat(userProfile.getPilotBusinessIdentifier(), is(pilot.getBusinessIdentifier()));

        assertThat(userProfile.getIndividualOperatorId(), is(individualOperator.getId()));
        assertThat(userProfile.getOperatorBusinessIdentifier(), is(individualOperator.getBusinessIdentifier()));

        assertThat(userProfile.getManufacturerId(), is(manufacturer.getId()));
        assertThat(userProfile.getManufacturerBusinessIdentifier(), is(manufacturer.getBusinessIdentifier()));
    }

    @Test
    public void shouldLoadUserOrganizationOperatorProfileById() throws Exception {
        //given
        long id = 1L;
        Pilot pilot = new Pilot(1L);
        OrganizationOperator organizationOperator = new OrganizationOperator(1, "", "name", "email", "9898121212", "123455", "India", emptyList());
        Manufacturer manufacturer = new Manufacturer(1, "", "name", "email", "9898121212", "123455", "India", emptyList());

        when(pilotRepository.loadByResourceOwner(id)).thenReturn(pilot);
        when(organizationOperatorRepository.loadByResourceOwner(id)).thenReturn(organizationOperator);
        when(manufacturerRepository.loadByResourceOwner(id)).thenReturn(manufacturer);

        //when
        UserProfile userProfile = service.profile(id);

        //then
        assertThat(userProfile.getPilotProfileId(), is(pilot.getId()));
        assertThat(userProfile.getPilotBusinessIdentifier(), is(pilot.getBusinessIdentifier()));

        assertThat(userProfile.getOrgOperatorId(), is(organizationOperator.getId()));
        assertThat(userProfile.getOperatorBusinessIdentifier(), is(organizationOperator.getBusinessIdentifier()));

        assertThat(userProfile.getManufacturerId(), is(manufacturer.getId()));
        assertThat(userProfile.getManufacturerBusinessIdentifier(), is(manufacturer.getBusinessIdentifier()));
    }

    @Test
    public void shouldResolveOperatorBusinessIdentifierForIndividualOperator() throws Exception {
        //given
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());
        when(individualOperatorRepository.findOne(1L)).thenReturn(individualOperator);

        //when
        String result = service.resolveOperatorBusinessIdentifier(ApplicantType.INDIVIDUAL, 1L);

        //then
        assertThat(result, is(individualOperator.getBusinessIdentifier()));
    }

    @Test
    public void shouldResolveOperatorBusinessIdentifierForOrgOperator() throws Exception {
        //given
        OrganizationOperator organizationOperator = new OrganizationOperator(1, "", "name", "email", "9898121212", "123455", "India", emptyList());
        when(organizationOperatorRepository.findOne(1L)).thenReturn(organizationOperator);

        //when
        String result = service.resolveOperatorBusinessIdentifier(ApplicantType.ORGANISATION, 1L);

        //then
        assertThat(result, is(organizationOperator.getBusinessIdentifier()));
    }
}