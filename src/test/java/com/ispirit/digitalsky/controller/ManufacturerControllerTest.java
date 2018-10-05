package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.Address;
import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.service.api.ManufacturerService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.InputStream;

import static com.ispirit.digitalsky.SecurityContextHelper.setUserSecurityContext;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ManufacturerController.class, secure = false)
@Import(TestContext.class)
public class ManufacturerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    ManufacturerService manufacturerService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;


    @Before
    public void setUp() throws Exception {
        userPrincipal = setUserSecurityContext();
    }

    @Test
    public void shouldValidateBeforeCreatingManufacturer() throws Exception {
        Manufacturer manufacturerPayload = new Manufacturer(0, null, null, null, null, null, null, null);
        String value =  objectMapper.writeValueAsString(manufacturerPayload);
        MockHttpServletResponse response  =
                mvc.perform(post("/api/manufacturer")
                                .param("manufacturer", value)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                        .andReturn().getResponse();
        int status =  response.getStatus();
        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldBeAbleToAddManufacturer() throws Exception {

        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Manufacturer manufacturerPayload = new Manufacturer(0, null, "Sample Name", "sample@email.com", "1234567", "9999999","India", asList(address));


        Manufacturer manufacturer = new Manufacturer(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "9999999", "India",  asList(address));
        manufacturer.setId(1);

        when(manufacturerService.createNewManufacturer(any(Manufacturer.class))).thenReturn(manufacturer);

        InputStream resource = this.getClass().getResourceAsStream("/trustedCertificateChain.pem");
        MockMultipartFile trustedCertificateDoc = new MockMultipartFile("trustedCertificateDoc", "trustedCertificateChain.pem", MediaType.MULTIPART_FORM_DATA_VALUE, resource);

        //when
        MockHttpServletResponse response = mvc.perform(
                fileUpload("/api/manufacturer").file(trustedCertificateDoc).param("manufacturer", objectMapper.writeValueAsString(manufacturerPayload))
        ).andReturn().getResponse();

        //then
        ArgumentCaptor<Manufacturer> argumentCaptor = ArgumentCaptor.forClass(Manufacturer.class);
        verify(manufacturerService).createNewManufacturer(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getResourceOwnerId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getTrustedCertificateDocName(), is("trustedCertificateDoc.pem"));
        assertThat(argumentCaptor.getValue().getTrustedCertificateDoc(), is(trustedCertificateDoc));

        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(objectMapper.readValue(response.getContentAsString(), Manufacturer.class).getId(), is(manufacturer.getId()));
    }

    @Test
    public void shouldValidateBeforeUpdatingManufacturerProfile() throws Exception {
        Manufacturer manufacturerPayload = new Manufacturer(userPrincipal.getId(), null, null, null, null, null, null, null);
        when(manufacturerService.find(1)).thenReturn(manufacturerPayload);
        when(manufacturerService.updateManufacturer(eq(1L), any(Manufacturer.class))).thenReturn(manufacturerPayload);

        MockHttpServletResponse response  =
                mvc.perform(put("/api/manufacturer/1")
                        .param("manufacturer", objectMapper.writeValueAsString(manufacturerPayload))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                        .andReturn().getResponse();
        int status =  response.getStatus();

        assertThat(status, is(HttpStatus.BAD_REQUEST.value()));

    }

    @Test
    public void shouldBeAbleToUpdateManufacturerProfile() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Manufacturer manufacturerPayload = new Manufacturer(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "999999999", "India", asList(address));
        manufacturerPayload.setId(1);

        when(manufacturerService.find(1)).thenReturn(manufacturerPayload);
        when(manufacturerService.updateManufacturer(eq(1L), any(Manufacturer.class))).thenReturn(manufacturerPayload);

        InputStream resource = this.getClass().getResourceAsStream("/trustedCertificateChain.pem");
        MockMultipartFile trustedCertificateDoc = new MockMultipartFile("trustedCertificateDoc", "trustedCertificateChain.pem", MediaType.MULTIPART_FORM_DATA_VALUE, resource);

        //when
        MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/manufacturer/1").file(trustedCertificateDoc).param("manufacturer", objectMapper.writeValueAsString(manufacturerPayload));
        ReflectionTestUtils.setField(requestBuilder,"method","PUT",String.class);
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        ArgumentCaptor<Manufacturer> argumentCaptor = ArgumentCaptor.forClass(Manufacturer.class);
        verify(manufacturerService).updateManufacturer(eq(1L), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getResourceOwnerId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getTrustedCertificateDocName(), is("trustedCertificateDoc.pem"));
        assertThat(argumentCaptor.getValue().getTrustedCertificateDoc(), is(trustedCertificateDoc));
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(objectMapper.readValue(response.getContentAsString(), Manufacturer.class).getId(), is(manufacturerPayload.getId()));
    }

    @Test
    public void shouldNotUpdateIfManufacturerProfileNotFound() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Manufacturer manufacturerPayload = new Manufacturer(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "999999999","India", asList(address));
        manufacturerPayload.setId(1);

        InputStream resource = this.getClass().getResourceAsStream("/trustedCertificateChain.pem");
        MockMultipartFile trustedCertificateDoc = new MockMultipartFile("trustedCertificateDoc", "trustedCertificateChain.pem", MediaType.MULTIPART_FORM_DATA_VALUE, resource);

        //when
        MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/manufacturer/1").file(trustedCertificateDoc).param("manufacturer", objectMapper.writeValueAsString(manufacturerPayload));
        ReflectionTestUtils.setField(requestBuilder,"method","PUT",String.class);
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        verify(manufacturerService, never()).updateManufacturer(eq(1L),any(Manufacturer.class));
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void shouldAllowOnlyResourceOwnerToUpdate() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        long resourceOwnerId = 2L;
        Manufacturer manufacturerPayload = new Manufacturer(resourceOwnerId, null, "Sample Name", "sample@email.com", "1234567" ,"9999999", "India", asList(address));
        manufacturerPayload.setId(1);

        when(manufacturerService.find(1)).thenReturn(manufacturerPayload);

        InputStream resource = this.getClass().getResourceAsStream("/trustedCertificateChain.pem");
        MockMultipartFile trustedCertificateDoc = new MockMultipartFile("trustedCertificateDoc", "trustedCertificateChain.pem", MediaType.MULTIPART_FORM_DATA_VALUE, resource);

        //when
        MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/manufacturer/1").file(trustedCertificateDoc).param("manufacturer", objectMapper.writeValueAsString(manufacturerPayload));
        ReflectionTestUtils.setField(requestBuilder,"method","PUT",String.class);
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        verify(manufacturerService, never()).updateManufacturer(eq(1L),any(Manufacturer.class));
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void shouldGetManufacturer() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Manufacturer manufacturer = new Manufacturer(userPrincipal.getId(), null, "Sample Name", "sample@email.com", "1234567", "99999999","India", asList(address));
        manufacturer.setId(12);
        when(manufacturerService.find(1)).thenReturn(manufacturer);

        //when
        MockHttpServletResponse response = mvc.perform(get("/api/manufacturer/1")).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        assertThat(objectMapper.readValue(response.getContentAsString(), Manufacturer.class).getId(), is(manufacturer.getId()));
    }

    @Test
    public void shouldAllowOnlyResourceOwnerToGet() throws Exception {
        //given
        Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");
        Manufacturer manufacturer = new Manufacturer(12, null, "Sample Name", "sample@email.com", "1234567", "99999999","India", asList(address));
        when(manufacturerService.find(1)).thenReturn(manufacturer);

        //when
        MockHttpServletResponse response = mvc.perform(get("/api/manufacturer/1")).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }


}