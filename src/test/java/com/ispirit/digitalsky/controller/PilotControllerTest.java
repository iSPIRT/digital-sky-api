package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.Address;
import com.ispirit.digitalsky.domain.DroneCategoryType;
import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.service.api.PilotService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static com.ispirit.digitalsky.SecurityContextHelper.setUserSecurityContext;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PilotController.class, secure = false)
@Import(TestContext.class)
public class PilotControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    PilotService pilotService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;


    @Before
    public void setUp() throws Exception {
        userPrincipal = setUserSecurityContext();
    }

    @Test
    public void shouldValidateBeforeCreatingPilot() throws Exception {
        Pilot pilotPayload = new Pilot(0, null, null, null, null, null, null, emptyList(), asList());

        InputStream resource = this.getClass().getResourceAsStream("/pilot_training_certificate.txt");
        MockMultipartFile trainingDoc = new MockMultipartFile("trainingCertificateDocument", "pilot_training_certificate.txt", MediaType.MULTIPART_FORM_DATA_VALUE, resource);

        //when
        MockHttpServletResponse response = mvc.perform(
                fileUpload("/api/pilot").file(trainingDoc).param("pilotPayload", objectMapper.writeValueAsString(pilotPayload))
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));

    }

    @Test
    public void shouldBeAbleToAddPilot() throws Exception {

        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Pilot pilotPayload = new Pilot(0, null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));


        Pilot pilot = new Pilot(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));
        pilot.setId(1);

        when(pilotService.createNewPilot(any(Pilot.class))).thenReturn(pilot);

        InputStream resource = this.getClass().getResourceAsStream("/pilot_training_certificate.txt");
        MockMultipartFile trainingDoc = new MockMultipartFile("trainingCertificateDocument", "pilot_training_certificate.txt", MediaType.MULTIPART_FORM_DATA_VALUE, resource);

        //when
        MockHttpServletResponse response = mvc.perform(
                fileUpload("/api/pilot").file(trainingDoc).param("pilotPayload", objectMapper.writeValueAsString(pilotPayload))
        ).andReturn().getResponse();

        //then
        ArgumentCaptor<Pilot> argumentCaptor = ArgumentCaptor.forClass(Pilot.class);
        verify(pilotService).createNewPilot(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getResourceOwnerId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getTrainingCertificateDocName(), is("trainingCertificateDocument.txt"));
        assertThat(argumentCaptor.getValue().getTrainingCertificate(), is(trainingDoc));

        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(objectMapper.readValue(response.getContentAsString(), Pilot.class).getId(), is(pilot.getId()));
    }

    @Test
    public void shouldValidateBeforeUpdatingPilotProfile() throws Exception {
        Pilot pilotPayload = new Pilot(userPrincipal.getId(), null, null, null, null, null, null, emptyList(), asList());
        pilotPayload.setId(1);

        when(pilotService.find(1)).thenReturn(pilotPayload);
        when(pilotService.updatePilot(eq(1L), any(Pilot.class))).thenReturn(pilotPayload);

        InputStream resource = this.getClass().getResourceAsStream("/pilot_training_certificate.txt");
        MockMultipartFile trainingDoc = new MockMultipartFile("trainingCertificateDocument", "pilot_training_certificate.txt", MediaType.MULTIPART_FORM_DATA_VALUE, resource);
        //when
        MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/pilot/1").file(trainingDoc).param("pilotPayload", objectMapper.writeValueAsString(pilotPayload));
        ReflectionTestUtils.setField(requestBuilder,"method","PUT",String.class);
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));

    }
    @Test
    public void shouldBeAbleToUpdatePilotProfile() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Pilot pilotPayload = new Pilot(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM, DroneCategoryType.LARGE), asList(address));
        pilotPayload.setId(1);

        when(pilotService.find(1)).thenReturn(pilotPayload);
        when(pilotService.updatePilot(eq(1L), any(Pilot.class))).thenReturn(pilotPayload);

        InputStream resource = this.getClass().getResourceAsStream("/pilot_training_certificate.txt");
        MockMultipartFile trainingDoc = new MockMultipartFile("trainingCertificateDocument", "pilot_training_certificate.txt", MediaType.MULTIPART_FORM_DATA_VALUE, resource);
        //when
        MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/pilot/1").file(trainingDoc).param("pilotPayload", objectMapper.writeValueAsString(pilotPayload));
        ReflectionTestUtils.setField(requestBuilder,"method","PUT",String.class);
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        ArgumentCaptor<Pilot> argumentCaptor = ArgumentCaptor.forClass(Pilot.class);
        verify(pilotService).updatePilot(eq(1L), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getResourceOwnerId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getTrainingCertificateDocName(), is("trainingCertificateDocument.txt"));
        assertThat(argumentCaptor.getValue().getTrainingCertificate(), is(trainingDoc));

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(objectMapper.readValue(response.getContentAsString(), Pilot.class).getId(), is(pilotPayload.getId()));
    }

    @Test
    public void shouldNotUpdateIfPilotProfileNotFound() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Pilot pilotPayload = new Pilot(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));
        pilotPayload.setId(1);

        InputStream resource = this.getClass().getResourceAsStream("/pilot_training_certificate.txt");
        MockMultipartFile trainingDoc = new MockMultipartFile("trainingCertificateDocument", "pilot_training_certificate.txt", MediaType.MULTIPART_FORM_DATA_VALUE, resource);
        //when
        MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/pilot/1").file(trainingDoc).param("pilotPayload", objectMapper.writeValueAsString(pilotPayload));
        ReflectionTestUtils.setField(requestBuilder,"method","PUT",String.class);
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        verify(pilotService, never()).updatePilot(eq(1L),any(Pilot.class));
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void shouldAllowOnlyResourceOwnerToUpdate() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        long resourceOwnerId = 2L;
        Pilot pilotPayload = new Pilot(resourceOwnerId, null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));
        pilotPayload.setId(1);

        when(pilotService.find(1)).thenReturn(pilotPayload);

        InputStream resource = this.getClass().getResourceAsStream("/pilot_training_certificate.txt");
        MockMultipartFile trainingDoc = new MockMultipartFile("trainingCertificateDocument", "pilot_training_certificate.txt", MediaType.MULTIPART_FORM_DATA_VALUE, resource);
        //when
        MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/pilot/1").file(trainingDoc).param("pilotPayload", objectMapper.writeValueAsString(pilotPayload));
        ReflectionTestUtils.setField(requestBuilder,"method","PUT",String.class);
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        verify(pilotService, never()).updatePilot(eq(1L),any(Pilot.class));
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void shouldGetPilot() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Pilot pilot = new Pilot(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));
        pilot.setId(12);
        when(pilotService.find(1)).thenReturn(pilot);

        //when
        MockHttpServletResponse response = mvc.perform(get("/api/pilot/1")).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(objectMapper.readValue(response.getContentAsString(), Pilot.class).getId(), is(pilot.getId()));
        assertThat(objectMapper.readValue(response.getContentAsString(), Pilot.class).getDroneCategoryTypes(), is(asList(DroneCategoryType.MEDIUM)));
    }

    @Test
    public void shouldAllowOnlyResourceOwnerToGet() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Pilot pilot = new Pilot(userPrincipal.getId() + 1, null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));
        pilot.setId(12);
        when(pilotService.find(1)).thenReturn(pilot);

        //when
        MockHttpServletResponse response = mvc.perform(get("/api/pilot/1")).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void shouldBeAbleToDownloadPilotTrainingCertificate() throws Exception {

        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Pilot pilot = new Pilot(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));
        pilot.setId(12);
        when(pilotService.find(1)).thenReturn(pilot);
        when(pilotService.trainingCertificate(any(Pilot.class))).thenReturn(new UrlResource(this.getClass().getResource("/pilot_training_certificate.txt")));

        //when
        MockHttpServletResponse response = mvc.perform(get("/api/pilot/1/trainingCertificate")).andReturn().getResponse();

        //then
        ArgumentCaptor<Pilot> argumentCaptor = ArgumentCaptor.forClass(Pilot.class);
        verify(pilotService).trainingCertificate(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId(), is(pilot.getId()));

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));

    }

    @Test
    public void shouldAllowOnlyResourceOwnerTODownloadPilotTrainingCertificate() throws Exception {

        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        long resourceOwnerId = 2L;
        Pilot pilot = new Pilot(resourceOwnerId, null, "Sample Name", "sample@email.com", "1234567", "India", LocalDate.of(1979, 10, 10), asList(DroneCategoryType.MEDIUM), asList(address));
        pilot.setId(12);
        when(pilotService.find(1)).thenReturn(pilot);

        //when
        MockHttpServletResponse response = mvc.perform(get("/api/pilot/1/trainingCertificate")).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));

    }
}