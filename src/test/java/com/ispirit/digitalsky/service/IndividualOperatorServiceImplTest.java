package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.exception.OperatorProfileAlreadyExist;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndividualOperatorServiceImplTest {

    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private IndividualOperatorServiceImpl service;

    @Before
    public void setUp() throws Exception {
        individualOperatorRepository = mock(IndividualOperatorRepository.class);
        organizationOperatorRepository = mock(OrganizationOperatorRepository.class);
        service = new IndividualOperatorServiceImpl(individualOperatorRepository, organizationOperatorRepository);
    }

    @Test
    public void shouldCheckForExistingIndividualOperatorProfileBeforeCreate() throws Exception {
        //given
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());
        when(individualOperatorRepository.loadByResourceOwner(individualOperator.getResourceOwnerId())).thenReturn(individualOperator);

        //when
        try {
            service.createNewOperator(individualOperator);
            fail("should have thrown OperatorProfileAlreadyExist");
        } catch (OperatorProfileAlreadyExist e) {
        }
    }

    @Test
    public void shouldCheckForExistingOrgOperatorProfileBeforeCreate() throws Exception {
        //given
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());
        OrganizationOperator organizationOperator = new OrganizationOperator(1,"","name","email","9898121212","123455","India",emptyList());
        when(organizationOperatorRepository.loadByResourceOwner(individualOperator.getResourceOwnerId())).thenReturn(organizationOperator);

        //when
        try {
            service.createNewOperator(individualOperator);
            fail("should have thrown OperatorProfileAlreadyExist");
        } catch (OperatorProfileAlreadyExist e) {
        }
    }

    @Test
    public void shouldCreateProfile() throws Exception {
        //given
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());

        //when
        service.createNewOperator(individualOperator);

        //then
        verify(individualOperatorRepository).save(individualOperator);
    }

    @Test
    public void shouldUpdateProfile() throws Exception {
        //given
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());

        //when
        service.updateOperator(1L, individualOperator);

        //then
        ArgumentCaptor<IndividualOperator> argumentCaptor = ArgumentCaptor.forClass(IndividualOperator.class);
        verify(individualOperatorRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId(), is(1L));
    }

    @Test
    public void shouldFindOperatorProfileById() throws Exception {
        //given
        IndividualOperator individualOperator = new IndividualOperator(1, "", "name", "email", "9880121212", "India", LocalDate.of(1988, 1, 1), emptyList());
        when(individualOperatorRepository.findOne(1L)).thenReturn(individualOperator);
        //when
        IndividualOperator result = service.find(1L);

        //then
        verify(individualOperatorRepository).findOne(1L);
        assertThat(result, is(individualOperator));
    }
}