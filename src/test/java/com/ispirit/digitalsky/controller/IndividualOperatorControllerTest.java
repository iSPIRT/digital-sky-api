package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.Address;
import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.OperatorProfileAlreadyExist;
import com.ispirit.digitalsky.service.api.IndividualOperatorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringRunner.class)
@WebMvcTest(value = IndividualOperatorController.class, secure = false)
@Import({TestContext.class})
public class IndividualOperatorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IndividualOperatorService individualOperatorService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = SecurityContextHelper.setUserSecurityContext();
    }

    @Test
    public void shouldCreateOperatorProfile() throws Exception {

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator operator = new IndividualOperator(
                0, "DEFAULT", "io1", "email", "mobile", "India", LocalDate.of(1982, 2, 2), asList(address));
        operator.setId(1);

        when(individualOperatorService.createNewOperator(any())).thenReturn(operator);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/operator")
                                .content(objectMapper.writeValueAsString(operator))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        ArgumentCaptor<IndividualOperator> argumentCaptor = ArgumentCaptor.forClass(IndividualOperator.class);
        verify(individualOperatorService).createNewOperator(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getResourceOwnerId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getName(), is(userPrincipal.getUsername()));
        assertThat(argumentCaptor.getValue().getEmail(), is(userPrincipal.getEmail()));
    }

    @Test
    public void shouldHandleExistingProfileError() throws Exception {

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator operator = new IndividualOperator(
                0, "DEFAULT", "io1", "email", "mobile", "India", LocalDate.of(1982, 2, 2), asList(address));

        when(individualOperatorService.createNewOperator(any())).thenThrow(new OperatorProfileAlreadyExist());

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/operator")
                                .content(objectMapper.writeValueAsString(operator))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.CONFLICT.value()));
    }

    @Test
    public void shouldUpdateOperatorProfileIfFound() throws Exception {

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator current = new IndividualOperator(
                userPrincipal.getId(), "DEFAULT", "io1", "email", "mobile", "India", LocalDate.of(1982, 2, 2), asList(address));
        current.setId(1);

        IndividualOperator operator = new IndividualOperator(
                0, "DEFAULT", "io1", "", "new-mobile", "India", LocalDate.of(1982, 2, 2), asList(address));

        when(individualOperatorService.find(1L)).thenReturn(current);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/operator/1")
                                .content(objectMapper.writeValueAsString(operator))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        ArgumentCaptor<IndividualOperator> argumentCaptor = ArgumentCaptor.forClass(IndividualOperator.class);
        verify(individualOperatorService).updateOperator(eq(1L), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getMobileNumber(), is(operator.getMobileNumber()));
        assertThat(argumentCaptor.getValue().getName(), is(current.getName()));
        assertThat(argumentCaptor.getValue().getEmail(), is(current.getEmail()));
        assertThat(argumentCaptor.getValue().getResourceOwnerId(), is(current.getResourceOwnerId()));
    }

    @Test
    public void shouldNotAllowUnAuthorizedUpdate() throws Exception {

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator current = new IndividualOperator(
                userPrincipal.getId() + 1, "DEFAULT", "io1", "email", "mobile", "India", LocalDate.of(1982, 2, 2), asList(address));
        current.setId(1);

        IndividualOperator operator = new IndividualOperator(
                0, "DEFAULT", "io1", "", "new-mobile", "India", LocalDate.of(1982, 2, 2), asList(address));

        when(individualOperatorService.find(1L)).thenReturn(current);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/operator/1")
                                .content(objectMapper.writeValueAsString(operator))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        verify(individualOperatorService, never()).updateOperator(eq(1L), any());
    }

    @Test
    public void shouldNotUpdateOperatorProfileIfNotFound() throws Exception {

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator operator = new IndividualOperator(
                0, "DEFAULT", "io1", "", "new-mobile", "India", LocalDate.of(1982, 2, 2), asList(address));

        when(individualOperatorService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/operator/1")
                                .content(objectMapper.writeValueAsString(operator))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        verify(individualOperatorService, never()).updateOperator(eq(1L), any());
    }

    @Test
    public void shouldGetOperatorProfileIfFound() throws Exception {

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator operator = new IndividualOperator(
                userPrincipal.getId(), "DEFAULT", "io1", "email", "mobile", "India", LocalDate.of(1982, 2, 2), asList(address));
        operator.setId(1);

        when(individualOperatorService.find(1L)).thenReturn(operator);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/operator/1")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        IndividualOperator responseBody = objectMapper.readValue(response.getContentAsString(), IndividualOperator.class);
        assertThat(responseBody.getId(), is(operator.getId()));
    }

    @Test
    public void shouldNotGetOperatorProfileIfNotFound() throws Exception {

        when(individualOperatorService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/operator/1")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }


    @Test
    public void shouldGetOperatorProfileIfCurrentUserIsAdmin() throws Exception {

        userPrincipal = SecurityContextHelper.setAdminUserSecurityContext();

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator operator = new IndividualOperator(
                this.userPrincipal.getId(), "DEFAULT", "io1", "email", "mobile", "India", LocalDate.of(1982, 2, 2), asList(address));
        operator.setId(1);

        when(individualOperatorService.find(1L)).thenReturn(operator);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/operator/1")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        IndividualOperator responseBody = objectMapper.readValue(response.getContentAsString(), IndividualOperator.class);
        assertThat(responseBody.getId(), is(operator.getId()));
    }

    @Test
    public void shouldNotAllowUnAuthorizedAccessToOperatorProfile() throws Exception {

        Address address = new Address("line1", "line2", "city", "state", "country", "560001");

        IndividualOperator operator = new IndividualOperator(
                userPrincipal.getId()+1, "DEFAULT", "io1", "email", "mobile", "India", LocalDate.of(1982, 2, 2), asList(address));
        operator.setId(1);

        when(individualOperatorService.find(1L)).thenReturn(operator);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        get("/api/operator/1")
                ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }
}