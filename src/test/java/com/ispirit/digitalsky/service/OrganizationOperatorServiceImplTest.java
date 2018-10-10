package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.exception.OperatorProfileAlreadyExist;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrganizationOperatorServiceImplTest {

    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private OrganizationOperatorServiceImpl service;

    @Before
    public void setUp() throws Exception {
        individualOperatorRepository = mock(IndividualOperatorRepository.class);
        organizationOperatorRepository = mock(OrganizationOperatorRepository.class);
        service = new OrganizationOperatorServiceImpl(organizationOperatorRepository, individualOperatorRepository);
    }

    @Test
    public void shouldCheckForExistingOrgOperatorProfileBeforeCreate() throws Exception {
        //given
        OrganizationOperator organizationOperator = new OrganizationOperator(1, "", "name", "email", "9898121212", "123455", "India", emptyList());
        when(organizationOperatorRepository.loadByResourceOwner(organizationOperator.getResourceOwnerId())).thenReturn(organizationOperator);

        //when
        try {
            service.createNewOperator(organizationOperator);
            fail("should have thrown OperatorProfileAlreadyExist");
        } catch (OperatorProfileAlreadyExist e) {
        }
    }

    @Test
    public void shouldCheckForExistingIndividualOperatorProfileBeforeCreate() throws Exception {
        //given
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());
        OrganizationOperator organizationOperator = new OrganizationOperator(1, "", "name", "email", "9898121212", "123455", "India", emptyList());
        when(individualOperatorRepository.loadByResourceOwner(organizationOperator.getResourceOwnerId())).thenReturn(individualOperator);

        //when
        try {
            service.createNewOperator(organizationOperator);
            fail("should have thrown OperatorProfileAlreadyExist");
        } catch (OperatorProfileAlreadyExist e) {
        }
    }

    @Test
    public void shouldCreateProfile() throws Exception {
        //given
        OrganizationOperator organizationOperator = new OrganizationOperator(1, "", "name", "email", "9898121212", "123455", "India", emptyList());

        //when
        service.createNewOperator(organizationOperator);

        //then
        verify(organizationOperatorRepository).save(organizationOperator);
    }

    @Test
    public void shouldUpdateProfile() throws Exception {
        //given
        OrganizationOperator organizationOperator = new OrganizationOperator(1, "", "name", "email", "9898121212", "123455", "India", emptyList());

        //when
        service.updateOperator(1L, organizationOperator);

        //then
        ArgumentCaptor<OrganizationOperator> argumentCaptor = ArgumentCaptor.forClass(OrganizationOperator.class);
        verify(organizationOperatorRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId(), is(1L));
    }

    @Test
    public void shouldFindOperatorProfileById() throws Exception {
        //given
        OrganizationOperator organizationOperator = new OrganizationOperator(1, "", "name", "email", "9898121212", "123455", "India", emptyList());
        when(organizationOperatorRepository.findOne(1L)).thenReturn(organizationOperator);
        //when
        OrganizationOperator result = service.find(1L);

        //then
        verify(organizationOperatorRepository).findOne(1L);
        assertThat(result, is(organizationOperator));
    }

}