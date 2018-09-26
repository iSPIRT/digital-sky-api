package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.service.api.UAOPApplicationService;
import org.apache.commons.io.IOUtils;
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
import org.springframework.security.util.InMemoryResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.ispirit.digitalsky.SecurityContextHelper.setUserSecurityContext;
import static com.ispirit.digitalsky.controller.UAOPApplicationController.UAOP_APPLICATION_RESOURCE_BASE_PATH;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UAOPApplicationController.class, secure = false)
@Import(TestContext.class)
public class UAOPApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    UAOPApplicationService uaopApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;


    @Before
    public void setUp() throws Exception {
        userPrincipal = setUserSecurityContext();
    }

    @Test
    public void shouldCreateApplication() throws Exception {

        InputStream sopDocContents = IOUtils.toInputStream("sopDocContents", "UTF-8");
        MockMultipartFile sopDoc = new MockMultipartFile("sopDoc", "sopDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, sopDocContents);

        InputStream securityProgramDocContents = IOUtils.toInputStream("securityProgramDocContents", "UTF-8");
        MockMultipartFile securityProgramDoc = new MockMultipartFile("securityProgramDoc", "securityProgramDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityProgramDocContents);

        InputStream insuranceDocContents = IOUtils.toInputStream("insuranceDocContents", "UTF-8");
        MockMultipartFile insuranceDoc = new MockMultipartFile("insuranceDoc", "insuranceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, insuranceDocContents);

        InputStream landOwnerPermissionDocContents = IOUtils.toInputStream("landOwnerPermissionDoc", "UTF-8");
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("landOwnerPermissionDoc", "landOwnerPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, landOwnerPermissionDocContents);

        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setName("name");
        uaopApplication.setDesignation("designation");

        //when
        MockHttpServletResponse response = mvc.perform(
                fileUpload(UAOP_APPLICATION_RESOURCE_BASE_PATH)
                        .file(sopDoc)
                        .file(securityProgramDoc)
                        .file(insuranceDoc)
                        .file(landOwnerPermissionDoc)
                        .param("uaopApplicationForm", objectMapper.writeValueAsString(uaopApplication))
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        ArgumentCaptor<UAOPApplication> argumentCaptor = ArgumentCaptor.forClass(UAOPApplication.class);
        verify(uaopApplicationService).createApplication(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getLandOwnerPermissionDocName(), is(landOwnerPermissionDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getLandOwnerPermissionDoc(), landOwnerPermissionDoc);

        assertThat(argumentCaptor.getValue().getSecurityProgramDocName(), is(securityProgramDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getSecurityProgramDoc(), securityProgramDoc);

        assertThat(argumentCaptor.getValue().getInsuranceDocName(), is(insuranceDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getInsuranceDoc(), insuranceDoc);

        assertThat(argumentCaptor.getValue().getSopDocName(), is(sopDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getSopDoc(), sopDoc);

    }

    @Test
    public void shouldValidateApplicationOnSubmitDuringCreate() throws Exception {

        InputStream sopDocContents = IOUtils.toInputStream("sopDocContents", "UTF-8");
        MockMultipartFile sopDoc = new MockMultipartFile("sopDoc", "sopDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, sopDocContents);

        InputStream securityProgramDocContents = IOUtils.toInputStream("securityProgramDocContents", "UTF-8");
        MockMultipartFile securityProgramDoc = new MockMultipartFile("securityProgramDoc", "securityProgramDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityProgramDocContents);

        InputStream insuranceDocContents = IOUtils.toInputStream("insuranceDocContents", "UTF-8");
        MockMultipartFile insuranceDoc = new MockMultipartFile("insuranceDoc", "insuranceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, insuranceDocContents);

        InputStream landOwnerPermissionDocContents = IOUtils.toInputStream("landOwnerPermissionDoc", "UTF-8");
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("landOwnerPermissionDoc", "landOwnerPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, landOwnerPermissionDocContents);

        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setStatus(ApplicationStatus.SUBMITTED);

        //when
        MockHttpServletResponse response = mvc.perform(
                fileUpload(UAOP_APPLICATION_RESOURCE_BASE_PATH)
                        .file(sopDoc)
                        .file(securityProgramDoc)
                        .file(insuranceDoc)
                        .file(landOwnerPermissionDoc)
                        .param("uaopApplicationForm", objectMapper.writeValueAsString(uaopApplication))
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));

    }

    @Test
    public void shouldUpdateApplication() throws Exception {

        InputStream sopDocContents = IOUtils.toInputStream("sopDocContents", "UTF-8");
        MockMultipartFile sopDoc = new MockMultipartFile("sopDoc", "sopDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, sopDocContents);

        InputStream securityProgramDocContents = IOUtils.toInputStream("securityProgramDocContents", "UTF-8");
        MockMultipartFile securityProgramDoc = new MockMultipartFile("securityProgramDoc", "securityProgramDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityProgramDocContents);

        InputStream insuranceDocContents = IOUtils.toInputStream("insuranceDocContents", "UTF-8");
        MockMultipartFile insuranceDoc = new MockMultipartFile("insuranceDoc", "insuranceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, insuranceDocContents);

        InputStream landOwnerPermissionDocContents = IOUtils.toInputStream("landOwnerPermissionDoc", "UTF-8");
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("landOwnerPermissionDoc", "landOwnerPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, landOwnerPermissionDocContents);

        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setName("name");
        uaopApplication.setDesignation("designation");

        UAOPApplication current = new UAOPApplication();
        current.setApplicantId(userPrincipal.getId());
        when(uaopApplicationService.get("1")).thenReturn(current);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(sopDoc)
                .file(securityProgramDoc)
                .file(insuranceDoc)
                .file(landOwnerPermissionDoc)
                .param("uaopApplicationForm", objectMapper.writeValueAsString(uaopApplication));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        ArgumentCaptor<UAOPApplication> argumentCaptor = ArgumentCaptor.forClass(UAOPApplication.class);
        verify(uaopApplicationService).updateApplication(eq("1"), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getLandOwnerPermissionDocName(), is(landOwnerPermissionDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getLandOwnerPermissionDoc(), landOwnerPermissionDoc);

        assertThat(argumentCaptor.getValue().getSecurityProgramDocName(), is(securityProgramDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getSecurityProgramDoc(), securityProgramDoc);

        assertThat(argumentCaptor.getValue().getInsuranceDocName(), is(insuranceDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getInsuranceDoc(), insuranceDoc);

        assertThat(argumentCaptor.getValue().getSopDocName(), is(sopDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getSopDoc(), sopDoc);

    }

    @Test
    public void shouldNotUpdateApplicationIfNotFound() throws Exception {

        InputStream sopDocContents = IOUtils.toInputStream("sopDocContents", "UTF-8");
        MockMultipartFile sopDoc = new MockMultipartFile("sopDoc", "sopDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, sopDocContents);

        InputStream securityProgramDocContents = IOUtils.toInputStream("securityProgramDocContents", "UTF-8");
        MockMultipartFile securityProgramDoc = new MockMultipartFile("securityProgramDoc", "securityProgramDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityProgramDocContents);

        InputStream insuranceDocContents = IOUtils.toInputStream("insuranceDocContents", "UTF-8");
        MockMultipartFile insuranceDoc = new MockMultipartFile("insuranceDoc", "insuranceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, insuranceDocContents);

        InputStream landOwnerPermissionDocContents = IOUtils.toInputStream("landOwnerPermissionDoc", "UTF-8");
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("landOwnerPermissionDoc", "landOwnerPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, landOwnerPermissionDocContents);

        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setName("name");
        uaopApplication.setDesignation("designation");

        when(uaopApplicationService.get("1")).thenReturn(null);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(sopDoc)
                .file(securityProgramDoc)
                .file(insuranceDoc)
                .file(landOwnerPermissionDoc)
                .param("uaopApplicationForm", objectMapper.writeValueAsString(uaopApplication));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void shouldNotUpdateApplicationIfUnAuthorizedAccess() throws Exception {

        InputStream sopDocContents = IOUtils.toInputStream("sopDocContents", "UTF-8");
        MockMultipartFile sopDoc = new MockMultipartFile("sopDoc", "sopDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, sopDocContents);

        InputStream securityProgramDocContents = IOUtils.toInputStream("securityProgramDocContents", "UTF-8");
        MockMultipartFile securityProgramDoc = new MockMultipartFile("securityProgramDoc", "securityProgramDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityProgramDocContents);

        InputStream insuranceDocContents = IOUtils.toInputStream("insuranceDocContents", "UTF-8");
        MockMultipartFile insuranceDoc = new MockMultipartFile("insuranceDoc", "insuranceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, insuranceDocContents);

        InputStream landOwnerPermissionDocContents = IOUtils.toInputStream("landOwnerPermissionDoc", "UTF-8");
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("landOwnerPermissionDoc", "landOwnerPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, landOwnerPermissionDocContents);

        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setName("name");
        uaopApplication.setDesignation("designation");

        UAOPApplication current = new UAOPApplication();
        current.setApplicantId(userPrincipal.getId()+1);
        when(uaopApplicationService.get("1")).thenReturn(current);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(sopDoc)
                .file(securityProgramDoc)
                .file(insuranceDoc)
                .file(landOwnerPermissionDoc)
                .param("uaopApplicationForm", objectMapper.writeValueAsString(uaopApplication));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void shouldValidateApplicationOnSubmitDuringUpdate() throws Exception {

        InputStream sopDocContents = IOUtils.toInputStream("sopDocContents", "UTF-8");
        MockMultipartFile sopDoc = new MockMultipartFile("sopDoc", "sopDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, sopDocContents);

        InputStream securityProgramDocContents = IOUtils.toInputStream("securityProgramDocContents", "UTF-8");
        MockMultipartFile securityProgramDoc = new MockMultipartFile("securityProgramDoc", "securityProgramDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityProgramDocContents);

        InputStream insuranceDocContents = IOUtils.toInputStream("insuranceDocContents", "UTF-8");
        MockMultipartFile insuranceDoc = new MockMultipartFile("insuranceDoc", "insuranceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, insuranceDocContents);

        InputStream landOwnerPermissionDocContents = IOUtils.toInputStream("landOwnerPermissionDoc", "UTF-8");
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("landOwnerPermissionDoc", "landOwnerPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, landOwnerPermissionDocContents);

        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setStatus(ApplicationStatus.SUBMITTED);

        UAOPApplication current = new UAOPApplication();
        current.setApplicantId(userPrincipal.getId());
        when(uaopApplicationService.get("1")).thenReturn(current);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(sopDoc)
                .file(securityProgramDoc)
                .file(insuranceDoc)
                .file(landOwnerPermissionDoc)
                .param("uaopApplicationForm", objectMapper.writeValueAsString(uaopApplication));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. BAD_REQUEST.value()));
    }

    @Test
    public void shouldApproveApplication() throws Exception {

        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId("1");
        approveRequestBody.setStatus(ApplicationStatus.APPROVED);


        //when
        MockHttpServletResponse response = mvc.perform(
                patch(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/approve/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveRequestBody))
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        ArgumentCaptor<ApproveRequestBody> argumentCaptor = ArgumentCaptor.forClass(ApproveRequestBody.class);
        verify(uaopApplicationService).approveApplication(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getApplicationFormId(), is(approveRequestBody.getApplicationFormId()));
        assertThat(argumentCaptor.getValue().getStatus(), is(approveRequestBody.getStatus()));
    }

    @Test
    public void shouldListApplicationsOfCurrentUser() throws Exception {

        List<UAOPApplication> applications = asList(new UAOPApplication(), new UAOPApplication());
        when(uaopApplicationService.getApplicationsOfApplicant(userPrincipal.getId())).thenReturn(applications);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/list")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        List<UAOPApplication> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<UAOPApplication>>() {
        });

        assertThat(result.size(), is(2));
    }

    @Test
    public void shouldGetAllApplicationsNotInDraftForAdmin() throws Exception {

        UAOPApplication uaopApplicationOne = new UAOPApplication();
        uaopApplicationOne.setStatus(ApplicationStatus.APPROVED);

        UAOPApplication uaopApplicationTwo = new UAOPApplication();
        uaopApplicationTwo.setStatus(ApplicationStatus.DRAFT);

        UAOPApplication uaopApplicationThree = new UAOPApplication();
        uaopApplicationThree.setStatus(ApplicationStatus.REJECTED);

        UAOPApplication uaopApplicationFour = new UAOPApplication();
        uaopApplicationFour.setStatus(ApplicationStatus.SUBMITTED);

        List<UAOPApplication> applications = asList(uaopApplicationOne, uaopApplicationTwo, uaopApplicationThree, uaopApplicationFour);
        when(uaopApplicationService.getAllApplications()).thenReturn(applications);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/getAll")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        List result = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(result.size(), is(3));
    }

    @Test
    public void shouldNotGetApplicationIfNotFound() throws Exception {

        when(uaopApplicationService.get("1")).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. NOT_FOUND.value()));
    }

    @Test
    public void shouldNotGetApplicationIfUnAuthorizedUser() throws Exception {

        UAOPApplication application = new UAOPApplication();
        application.setApplicantId(userPrincipal.getId()+1);
        when(uaopApplicationService.get("1")).thenReturn(application);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. UNAUTHORIZED.value()));
    }

    @Test
    public void shouldGetApplicationIfNotFound() throws Exception {

        UAOPApplication application = new UAOPApplication();
        application.setName("name");
        application.setApplicantId(userPrincipal.getId());

        when(uaopApplicationService.get("1")).thenReturn(application);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        UAOPApplication result = objectMapper.readValue(response.getContentAsString(), UAOPApplication.class);
        assertThat(result.getName(), is(application.getName()));
    }

    @Test
    public void shouldNotGetFileIfApplicationNotFound() throws Exception {

        when(uaopApplicationService.get("1")).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
        ).andReturn().getResponse();

        verify(uaopApplicationService, never()).getFile(anyString(), anyString());
    }

    @Test
    public void shouldNotGetFileIfUnAuthorizedUser() throws Exception {

        UAOPApplication application = new UAOPApplication();
        application.setApplicantId(userPrincipal.getId()+1);
        when(uaopApplicationService.get("1")).thenReturn(application);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
        ).andReturn().getResponse();

        verify(uaopApplicationService, never()).getFile(anyString(), anyString());

    }

    @Test
    public void shouldtGetFileIfAdminUser() throws Exception {
        userPrincipal = SecurityContextHelper.setAdminUserSecurityContext();

        UAOPApplication application = new UAOPApplication();
        application.setApplicantId(userPrincipal.getId()+1);
        when(uaopApplicationService.get("1")).thenReturn(application);

        when(uaopApplicationService.getFile("1","sopDoc.txt")).thenReturn(new InMemoryResource("test"));
        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
        ).andReturn().getResponse();

        assertThat(response.getContentAsString(), is("test"));
    }

    @Test
    public void shouldGetFile() throws Exception {

        UAOPApplication application = new UAOPApplication();
        application.setApplicantId(userPrincipal.getId());
        when(uaopApplicationService.get("1")).thenReturn(application);

        when(uaopApplicationService.getFile("1","sopDoc.txt")).thenReturn(new InMemoryResource("test"));

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UAOP_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
        ).andReturn().getResponse();

        assertThat(response.getContentAsString(), is("test"));
    }




    private void assertFileContents(MultipartFile file, MockMultipartFile expected) throws IOException {
        assertThat(IOUtils.toString(file.getInputStream(), "UTF-8"), is(IOUtils.toString(expected.getInputStream(), "UTF-8")));
    }
}