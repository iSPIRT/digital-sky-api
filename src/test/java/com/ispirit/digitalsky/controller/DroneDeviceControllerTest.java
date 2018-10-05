package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.service.api.DroneDeviceService;
import com.ispirit.digitalsky.service.api.UserProfileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static com.ispirit.digitalsky.SecurityContextHelper.setUserSecurityContext;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(value = DroneDeviceController.class, secure = false)
@Import(TestContext.class)
public class DroneDeviceControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    DroneDeviceService droneDeviceService;

    @MockBean
    UserProfileService userProfileService;

    private UserPrincipal userPrincipal;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp()  {

    }

    @Test
    public void shouldValidateBeforeRegistering() throws Exception {
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(null, null, null);
        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                    mvc.perform(post("/api/droneDevice/register/1")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();
        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldRegisterDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        DroneDevice createdMockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        createdMockDrone.setManufacturerBusinessIdentifier("2ff217e740534fde89c1bfe62e08f317");

        when(droneDeviceService.register(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenReturn(createdMockDrone);

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/droneDevice/register/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.CREATED.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(responsePayload.getTxn(), is(createdMockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.REGISTERED));
    }

    @Test
    public void shouldThrowInvalidDigitalSignatureExceptionBeforeRegisteringDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.register(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new InvalidDigitalSignatureException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/droneDevice/register/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.INVALID_SIGNATURE));
        assertThat(responsePayload.getError().getErrors().get(0), is("Invalid/Missing digital signature"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));
    }

    @Test
    public void shouldThrowOperatorBusinessIdentifierMissingExceptionBeforeRegisteringDrone() throws Exception {

        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.register(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new OperatorBusinessIdentifierMissingException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/droneDevice/register/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.OPERATOR_BUSINESS_IDENTIFIER_MISSING));
        assertThat(responsePayload.getError().getErrors().get(0), is("Operator business identifier missing in the payload"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));

    }
    @Test
    public void shouldThrowInvalidOperatorBusinessIdentifierExceptionBeforeRegisteringDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.register(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new InvalidOperatorBusinessIdentifierException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/droneDevice/register/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.OPERATOR_BUSINESS_IDENTIFIER_INVALID));
        assertThat(responsePayload.getError().getErrors().get(0), is("Invalid operator business identifier"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));
    }

    @Test
    public void shouldThrowDroneDeviceAlreadyExistExceptionBeforeRegisteringDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.register(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new DroneDeviceAlreadyExistException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/droneDevice/register/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.DRONE_ALREADY_REGISTERED));
        assertThat(responsePayload.getError().getErrors().get(0), is("Drone already registered"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));

    }
    @Test
    public void shouldThrowInvalidManufacturerExceptionBeforeRegisteringDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.register(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new InvalidManufacturerException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/droneDevice/register/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.INVALID_MANUFACTURER));
        assertThat(responsePayload.getError().getErrors().get(0), is("Invalid manufacturer"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));

    }
    @Test
    public void shouldThrowManufacturerNotFoundExceptionBeforeRegisteringDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.register(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new ManufacturerNotFoundException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/droneDevice/register/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.MANUFACTURER_BUSINESS_IDENTIFIER_INVALID));
        assertThat(responsePayload.getError().getErrors().get(0), is("Manufacturer not found"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));

    }

    @Test
    public void shouldValidateBeforeDeRegistering() throws Exception {
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(null, "", "");
        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/1")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();
        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldDeregisterDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        DroneDevice updatedMockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        updatedMockDrone.setManufacturerBusinessIdentifier("2ff217e740534fde89c1bfe62e08f317");

        when(droneDeviceService.deregister(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenReturn(updatedMockDrone);

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.OK.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(responsePayload.getTxn(), is(updatedMockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.DEREGISTERED));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));

    }

    @Test
    public void shouldThrowInvalidDigitalSignatureExceptionBeforeDeRegisteringDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.deregister(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new InvalidDigitalSignatureException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.INVALID_SIGNATURE));
        assertThat(responsePayload.getError().getErrors().get(0), is("Invalid/Missing digital signature"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));
    }

    @Test
    public void shouldThrowDeviceNotInRegisteredStateExceptionBeforeDeRegisteringDrone() throws Exception {

        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.deregister(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new DeviceNotInRegisteredStateException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.DRONE_NOT_REGISTERED));
        assertThat(responsePayload.getError().getErrors().get(0), is("Device not registered"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));

    }

    @Test
    public void shouldThrowInvalidDigitalCertificateExceptionBeforeDeRegisteringDrone() throws Exception {

        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.deregister(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new InvalidDigitalCertificateException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.INVALID_DIGITAL_CERTIFICATE));
        assertThat(responsePayload.getError().getErrors().get(0), is("Invalid digital certificate"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));

    }

    @Test
    public void shouldThrowDroneDeviceNotFoundExceptionBeforeDeRegisteringDrone() throws Exception {
        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.deregister(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new DroneDeviceNotFoundException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.DRONE_NOT_FOUND));
        assertThat(responsePayload.getError().getErrors().get(0), is("Drone not found"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));
    }

    @Test
    public void shouldThrowManufacturerNotFoundExceptionBeforeDeRegisteringDrone() throws Exception {

        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.deregister(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new ManufacturerNotFoundException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.MANUFACTURER_BUSINESS_IDENTIFIER_INVALID));
        assertThat(responsePayload.getError().getErrors().get(0), is("Manufacturer not found"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));
    }

    @Test
    public void shouldThrowInvalidManufacturerExceptionBeforeDeRegisteringDrone() throws Exception {

        DroneDevice mockDrone = new DroneDevice("1.0","Beebop 800.0","1A29.0", "From manufacturer ", "eff217e740534fde89c1bfe62e08f316", "some value");
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(mockDrone, "", "");

        when(droneDeviceService.deregister(eq("2ff217e740534fde89c1bfe62e08f317"),any(RegisterDroneRequestPayload.class))).thenThrow(new InvalidManufacturerException());

        final String requestPayloadString = objectMapper.writeValueAsString(requestPayload);
        MockHttpServletResponse response  =
                mvc.perform(patch("/api/droneDevice/deregister/2ff217e740534fde89c1bfe62e08f317")
                        .content(requestPayloadString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        int status =  response.getStatus();

        RegisterDroneResponsePayload responsePayload = objectMapper.readValue(response.getContentAsString(), RegisterDroneResponsePayload.class);

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
        assertThat(responsePayload.getTxn(), is(mockDrone.getTxn()));
        assertThat(responsePayload.getResponseCode(), is(RegisterDroneResponseCode.INVALID_MANUFACTURER));
        assertThat(responsePayload.getError().getErrors().get(0), is("Invalid manufacturer"));
        assertThat(responsePayload.getResponseTimeStamp().toLocalDate(), is(LocalDate.now()));
    }

    @Test
    public void shouldListDroneDevicesForOperator() throws Exception {
        userPrincipal = setUserSecurityContext();
        when(userProfileService.profile(eq(1L))).thenReturn(new UserProfile(1,0,0,1,0,null,"eff217e740534fde89c1bfe62e08f316", null));
        when(droneDeviceService.getRegisteredDroneDeviceIds(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(asList("1", "2"));

        MockHttpServletResponse response  =
                mvc.perform(get("/api/droneDevice/list")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        String content = response.getContentAsString();

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(content, is("[\"1\",\"2\"]"));
    }

    @Test
    public void shouldThrowExceptionForListingDronesIfProfileIsNull() throws Exception {
        userPrincipal = setUserSecurityContext();
        when(userProfileService.profile(eq(1L))).thenReturn(null);

        MockHttpServletResponse response  =
                mvc.perform(get("/api/droneDevice/list")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        Errors error = objectMapper.readValue(response.getContentAsString(), Errors.class);
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(error.getErrors().get(0), is("Invalid operator business identifier"));
        verify(droneDeviceService, never()).getRegisteredDroneDeviceIds(any());
    }

    @Test
    public void shouldThrowExceptionForListingDronesIfNotOperatorProfile() throws Exception {
        userPrincipal = setUserSecurityContext();
        when(userProfileService.profile(eq(1L))).thenReturn(new UserProfile(1,1,0,0,0,null,null, null));

        MockHttpServletResponse response  =
                mvc.perform(get("/api/droneDevice/list")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse();

        Errors error = objectMapper.readValue(response.getContentAsString(), Errors.class);
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(error.getErrors().get(0), is("User not an operator"));
        verify(droneDeviceService, never()).getRegisteredDroneDeviceIds(any());
    }

}
