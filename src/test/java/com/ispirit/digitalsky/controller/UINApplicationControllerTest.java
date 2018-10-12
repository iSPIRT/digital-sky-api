package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.document.UINApplication;
import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.OperatorNotAuthorizedException;
import com.ispirit.digitalsky.service.api.UINApplicationService;
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
import static com.ispirit.digitalsky.controller.UINApplicationController.UIN_APPLICATION_RESOURCE_BASE_PATH;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UINApplicationController.class, secure = false)
@Import(TestContext.class)
public class UINApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UINApplicationService uinApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;


    @Before
    public void setUp() throws Exception {
        userPrincipal = setUserSecurityContext();
    }

    @Test
    public void shouldCreateApplication() throws Exception {

        InputStream importPermissionDocContent = IOUtils.toInputStream("importPermissionDocContent", "UTF-8");
        MockMultipartFile importPermissionDoc = new MockMultipartFile("importPermissionDoc", "importPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, importPermissionDocContent);

        InputStream cinDocContents = IOUtils.toInputStream("cinDocContents", "UTF-8");
        MockMultipartFile cinDoc = new MockMultipartFile("cinDoc", "cinDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, cinDocContents);

        InputStream gstinDocContents = IOUtils.toInputStream("gstinDocContents", "UTF-8");
        MockMultipartFile gstinDoc = new MockMultipartFile("gstinDoc", "gstinDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, gstinDocContents);

        InputStream panCardDocContents = IOUtils.toInputStream("panCardDocContent", "UTF-8");
        MockMultipartFile panCardDoc = new MockMultipartFile("panCardDoc", "panCardDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, panCardDocContents);

        InputStream dotPermissionDocContents = IOUtils.toInputStream("dotPermissionDocContents", "UTF-8");
        MockMultipartFile dotPermissionDoc = new MockMultipartFile("dotPermissionDoc", "dotPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, dotPermissionDocContents);

        InputStream securityClearanceDocContents = IOUtils.toInputStream("securityClearanceDocContents", "UTF-8");
        MockMultipartFile securityClearanceDoc = new MockMultipartFile("securityClearanceDoc", "securityClearanceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityClearanceDocContents);

        InputStream etaDocContents = IOUtils.toInputStream("etaDocContents", "UTF-8");
        MockMultipartFile etaDoc = new MockMultipartFile("etaDoc", "etaDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, etaDocContents);

        InputStream opManualDocContents = IOUtils.toInputStream("opManualDocContents", "UTF-8");
        MockMultipartFile opManualDoc = new MockMultipartFile("opManualDoc", "opManualDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, opManualDocContents);

        InputStream maintenanceGuidelinesDocContents = IOUtils.toInputStream("maintenanceGuidelinesDocContents", "UTF-8");
        MockMultipartFile maintenanceGuidelinesDoc = new MockMultipartFile("maintenanceGuidelinesDoc", "maintenanceGuidelinesDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, maintenanceGuidelinesDocContents);

        UINApplication uinApplication = new UINApplication();
        uinApplication.setApplicantId(1L);
        uinApplication.setOperatorId(2L);
        uinApplication.setFeeDetails("cheque");
        uinApplication.setApplicantType(ApplicantType.INDIVIDUAL);

        //when
        MockHttpServletResponse response = mvc.perform(
                fileUpload(UIN_APPLICATION_RESOURCE_BASE_PATH)
                        .file(importPermissionDoc)
                        .file(cinDoc)
                        .file(gstinDoc)
                        .file(panCardDoc)
                        .file(dotPermissionDoc)
                        .file(securityClearanceDoc)
                        .file(etaDoc)
                        .file(opManualDoc)
                        .file(maintenanceGuidelinesDoc)
                        .param("uinApplication", objectMapper.writeValueAsString(uinApplication))
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        ArgumentCaptor<UINApplication> argumentCaptor = ArgumentCaptor.forClass(UINApplication.class);
        verify(uinApplicationService).createApplication(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getImportPermissionDocName(), is(importPermissionDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getImportPermissionDoc(), importPermissionDoc);

        assertThat(argumentCaptor.getValue().getCinDocName(), is(cinDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getCinDoc(), cinDoc);

        assertThat(argumentCaptor.getValue().getGstinDocName(), is(gstinDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getGstinDoc(), gstinDoc);

        assertThat(argumentCaptor.getValue().getPanCardDocName(), is(panCardDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getPanCardDoc(), panCardDoc);

        assertThat(argumentCaptor.getValue().getDotPermissionDocName(), is(dotPermissionDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getDotPermissionDoc(), dotPermissionDoc);

        assertThat(argumentCaptor.getValue().getSecurityClearanceDocName(), is(securityClearanceDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getSecurityClearanceDoc(), securityClearanceDoc);

        assertThat(argumentCaptor.getValue().getEtaDocName(), is(etaDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getEtaDoc(), etaDoc);

        assertThat(argumentCaptor.getValue().getOpManualDocName(), is(opManualDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getOpManualDoc(), opManualDoc);

        assertThat(argumentCaptor.getValue().getMaintenanceGuidelinesDocName(), is(maintenanceGuidelinesDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getMaintenanceGuidelinesDoc(), maintenanceGuidelinesDoc);

    }

    @Test
    public void shouldHandleOperatorNotAuthorizedExceptionWhileCreatingApplication() throws Exception {
        InputStream importPermissionDocContent = IOUtils.toInputStream("importPermissionDocContent", "UTF-8");
        MockMultipartFile importPermissionDoc = new MockMultipartFile("importPermissionDoc", "importPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, importPermissionDocContent);

        UINApplication uinApplication = new UINApplication();
        uinApplication.setApplicantId(1L);
        uinApplication.setOperatorId(2L);
        uinApplication.setFeeDetails("checque");
        uinApplication.setApplicantType(ApplicantType.INDIVIDUAL);
        when(uinApplicationService.createApplication(any(UINApplication.class))).thenThrow(new OperatorNotAuthorizedException());

        //when

        MockHttpServletResponse response = mvc.perform(
                fileUpload(UIN_APPLICATION_RESOURCE_BASE_PATH)
                        .file(importPermissionDoc)
                        .param("uinApplication", objectMapper.writeValueAsString(uinApplication))
        ).andReturn().getResponse();

        //then

        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
        assertThat(objectMapper.readValue(response.getContentAsString(), Errors.class).getErrors().get(0), is("Device not assigned to the operator"));

    }

    @Test
    public void shouldHandleValidationExceptionWhileCreatingApplication() throws Exception {

        InputStream importPermissionDocContent = IOUtils.toInputStream("importPermissionDocContent", "UTF-8");
        MockMultipartFile importPermissionDoc = new MockMultipartFile("importPermissionDoc", "importPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, importPermissionDocContent);

        UINApplication uinApplication = new UINApplication();
        uinApplication.setApplicantId(1L);
        uinApplication.setOperatorId(2L);
        uinApplication.setApplicantType(ApplicantType.INDIVIDUAL);

        //when

        MockHttpServletResponse response = mvc.perform(
                fileUpload(UIN_APPLICATION_RESOURCE_BASE_PATH)
                        .file(importPermissionDoc)
                        .param("uinApplication", objectMapper.writeValueAsString(uinApplication))
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.PRECONDITION_FAILED.value()));
        assertThat(objectMapper.readValue(response.getContentAsString(), Errors.class).getErrors().get(0), is("feeDetails may not be null"));
    }

    @Test
    public void shouldValidateUpdateApplication() throws Exception {

        InputStream importPermissionDocContents = IOUtils.toInputStream("importPermissionDocContents", "UTF-8");
        MockMultipartFile importPermissionDoc = new MockMultipartFile("importPermissionDoc", "importPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, importPermissionDocContents);

        UINApplication uinApplication = new UINApplication();
        uinApplication.setStatus(ApplicationStatus.SUBMITTED);

        UINApplication current = new UINApplication();
        current.setApplicantId(userPrincipal.getId());
        when(uinApplicationService.get("1")).thenReturn(current);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(importPermissionDoc)
                .param("uinApplication", objectMapper.writeValueAsString(uinApplication));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldUpdateApplication() throws Exception {

        InputStream importPermissionDocContent = IOUtils.toInputStream("importPermissionDocContent", "UTF-8");
        MockMultipartFile importPermissionDoc = new MockMultipartFile("importPermissionDoc", "importPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, importPermissionDocContent);

        InputStream cinDocContents = IOUtils.toInputStream("cinDocContents", "UTF-8");
        MockMultipartFile cinDoc = new MockMultipartFile("cinDoc", "cinDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, cinDocContents);

        InputStream gstinDocContents = IOUtils.toInputStream("gstinDocContents", "UTF-8");
        MockMultipartFile gstinDoc = new MockMultipartFile("gstinDoc", "gstinDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, gstinDocContents);

        InputStream panCardDocContents = IOUtils.toInputStream("panCardDocContent", "UTF-8");
        MockMultipartFile panCardDoc = new MockMultipartFile("panCardDoc", "panCardDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, panCardDocContents);

        InputStream dotPermissionDocContents = IOUtils.toInputStream("dotPermissionDocContents", "UTF-8");
        MockMultipartFile dotPermissionDoc = new MockMultipartFile("dotPermissionDoc", "dotPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, dotPermissionDocContents);

        InputStream securityClearanceDocContents = IOUtils.toInputStream("securityClearanceDocContents", "UTF-8");
        MockMultipartFile securityClearanceDoc = new MockMultipartFile("securityClearanceDoc", "securityClearanceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityClearanceDocContents);

        InputStream etaDocContents = IOUtils.toInputStream("etaDocContents", "UTF-8");
        MockMultipartFile etaDoc = new MockMultipartFile("etaDoc", "etaDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, etaDocContents);

        InputStream opManualDocContents = IOUtils.toInputStream("opManualDocContents", "UTF-8");
        MockMultipartFile opManualDoc = new MockMultipartFile("opManualDoc", "opManualDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, opManualDocContents);

        InputStream maintenanceGuidelinesDocContents = IOUtils.toInputStream("maintenanceGuidelinesDocContents", "UTF-8");
        MockMultipartFile maintenanceGuidelinesDoc = new MockMultipartFile("maintenanceGuidelinesDoc", "maintenanceGuidelinesDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, maintenanceGuidelinesDocContents);

        UINApplication uinApplication = new UINApplication();
        uinApplication.setApplicantId(1L);
        uinApplication.setOperatorId(2L);
        uinApplication.setApplicantType(ApplicantType.INDIVIDUAL);

        UINApplication current = new UINApplication();
        current.setApplicantId(userPrincipal.getId());
        when(uinApplicationService.get("1")).thenReturn(current);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(importPermissionDoc)
                .file(cinDoc)
                .file(gstinDoc)
                .file(panCardDoc)
                .file(dotPermissionDoc)
                .file(securityClearanceDoc)
                .file(etaDoc)
                .file(opManualDoc)
                .file(maintenanceGuidelinesDoc)
                .param("uinApplication", objectMapper.writeValueAsString(uinApplication));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        ArgumentCaptor<UINApplication> argumentCaptor = ArgumentCaptor.forClass(UINApplication.class);
        verify(uinApplicationService).updateApplication(eq("1"), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getImportPermissionDocName(), is(importPermissionDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getImportPermissionDoc(), importPermissionDoc);

        assertThat(argumentCaptor.getValue().getCinDocName(), is(cinDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getCinDoc(), cinDoc);

        assertThat(argumentCaptor.getValue().getGstinDocName(), is(gstinDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getGstinDoc(), gstinDoc);

        assertThat(argumentCaptor.getValue().getPanCardDocName(), is(panCardDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getPanCardDoc(), panCardDoc);

        assertThat(argumentCaptor.getValue().getDotPermissionDocName(), is(dotPermissionDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getDotPermissionDoc(), dotPermissionDoc);

        assertThat(argumentCaptor.getValue().getSecurityClearanceDocName(), is(securityClearanceDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getSecurityClearanceDoc(), securityClearanceDoc);

        assertThat(argumentCaptor.getValue().getEtaDocName(), is(etaDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getEtaDoc(), etaDoc);

        assertThat(argumentCaptor.getValue().getOpManualDocName(), is(opManualDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getOpManualDoc(), opManualDoc);

        assertThat(argumentCaptor.getValue().getMaintenanceGuidelinesDocName(), is(maintenanceGuidelinesDoc.getName() + ".txt"));
        assertFileContents(argumentCaptor.getValue().getMaintenanceGuidelinesDoc(), maintenanceGuidelinesDoc);

    }

    @Test
    public void shouldNotUpdateApplicationIfNotFound() throws Exception {

        InputStream importPermissionDocContent = IOUtils.toInputStream("importPermissionDocContent", "UTF-8");
        MockMultipartFile importPermissionDoc = new MockMultipartFile("importPermissionDoc", "importPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, importPermissionDocContent);

        UINApplication uinApplication = new UINApplication();
        uinApplication.setApplicantId(1L);
        uinApplication.setOperatorId(2L);
        uinApplication.setApplicantType(ApplicantType.INDIVIDUAL);

        when(uinApplicationService.get("1")).thenReturn(null);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(importPermissionDoc)
                .param("uinApplication", objectMapper.writeValueAsString(uinApplication));
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

        InputStream importPermissionDocContent = IOUtils.toInputStream("importPermissionDocContent", "UTF-8");
        MockMultipartFile importPermissionDoc = new MockMultipartFile("importPermissionDoc", "importPermissionDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, importPermissionDocContent);

        UINApplication uinApplication = new UINApplication();
        uinApplication.setApplicantId(1L);
        uinApplication.setOperatorId(2L);
        uinApplication.setApplicantType(ApplicantType.INDIVIDUAL);

        UINApplication current = new UINApplication();
        current.setApplicantId(userPrincipal.getId()+1);
        when(uinApplicationService.get("1")).thenReturn(current);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
                .file(importPermissionDoc)
                .param("uinApplication", objectMapper.writeValueAsString(uinApplication));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void shouldApproveApplication() throws Exception {

        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId("1");
        approveRequestBody.setStatus(ApplicationStatus.APPROVED);

        //when
        MockHttpServletResponse response = mvc.perform(
                patch(UIN_APPLICATION_RESOURCE_BASE_PATH + "/approve/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveRequestBody))
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        ArgumentCaptor<ApproveRequestBody> argumentCaptor = ArgumentCaptor.forClass(ApproveRequestBody.class);
        verify(uinApplicationService).approveApplication(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getApplicationFormId(), is(approveRequestBody.getApplicationFormId()));
        assertThat(argumentCaptor.getValue().getStatus(), is(approveRequestBody.getStatus()));
    }

    @Test
    public void shouldListApplicationsOfCurrentUser() throws Exception {

        List<UINApplication> applications = asList(new UINApplication(), new UINApplication());
        when(uinApplicationService.getApplicationsOfApplicant(userPrincipal.getId())).thenReturn(applications);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/list")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        List<UAOPApplication> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<UINApplication>>() {
        });

        assertThat(result.size(), is(2));
    }

    @Test
    public void shouldGetAllApplicationsNotInDraftForAdmin() throws Exception {

        UINApplication uinApplicationOne = new UINApplication();
        uinApplicationOne.setStatus(ApplicationStatus.APPROVED);

        UINApplication uinApplicationTwo = new UINApplication();
        uinApplicationTwo.setStatus(ApplicationStatus.DRAFT);

        UINApplication uinApplicationThree = new UINApplication();
        uinApplicationThree.setStatus(ApplicationStatus.REJECTED);

        UINApplication uinApplicationFour = new UINApplication();
        uinApplicationFour.setStatus(ApplicationStatus.SUBMITTED);

        List<UINApplication> applications = asList(uinApplicationOne, uinApplicationTwo, uinApplicationThree, uinApplicationFour);
        when(uinApplicationService.getAllApplications()).thenReturn(applications);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/getAll")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        List result = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(result.size(), is(3));
    }

    @Test
    public void shouldNotGetApplicationIfNotFound() throws Exception {

        when(uinApplicationService.get("1")).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. NOT_FOUND.value()));
    }

    @Test
    public void shouldNotGetApplicationIfUnAuthorizedUser() throws Exception {

        UINApplication application = new UINApplication();
        application.setApplicantId(userPrincipal.getId()+1);
        when(uinApplicationService.get("1")).thenReturn(application);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. UNAUTHORIZED.value()));
    }

    @Test
    public void shouldGetApplicationIfFound() throws Exception {

        UINApplication application = new UINApplication();
        application.setModelName("model");
        application.setApplicantId(userPrincipal.getId());

        when(uinApplicationService.get("1")).thenReturn(application);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
        ).andReturn().getResponse();

        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
        UINApplication result = objectMapper.readValue(response.getContentAsString(), UINApplication.class);
        assertThat(result.getModelName(), is(application.getModelName()));
    }

    @Test
    public void shouldNotGetFileIfApplicationNotFound() throws Exception {

        when(uinApplicationService.get("1")).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1/document/importPermissionDoc.txt")
        ).andReturn().getResponse();

        verify(uinApplicationService, never()).getFile(anyString(), anyString());
    }

    @Test
    public void shouldNotGetFileIfUnAuthorizedUser() throws Exception {

        UINApplication application = new UINApplication();
        application.setApplicantId(userPrincipal.getId()+1);
        when(uinApplicationService.get("1")).thenReturn(application);

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
        ).andReturn().getResponse();

        verify(uinApplicationService, never()).getFile(anyString(), anyString());

    }

    @Test
    public void shouldtGetFileIfAdminUser() throws Exception {
        userPrincipal = SecurityContextHelper.setAdminUserSecurityContext();

        UINApplication application = new UINApplication();
        application.setApplicantId(userPrincipal.getId()+1);
        when(uinApplicationService.get("1")).thenReturn(application);

        when(uinApplicationService.getFile("1","sopDoc.txt")).thenReturn(new InMemoryResource("test"));
        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
        ).andReturn().getResponse();

        assertThat(response.getContentAsString(), is("test"));
    }

    @Test
    public void shouldGetFile() throws Exception {

        UINApplication application = new UINApplication();
        application.setApplicantId(userPrincipal.getId());
        when(uinApplicationService.get("1")).thenReturn(application);

        when(uinApplicationService.getFile("1","sopDoc.txt")).thenReturn(new InMemoryResource("test"));

        //when
        MockHttpServletResponse response = mvc.perform(
                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
        ).andReturn().getResponse();

        assertThat(response.getContentAsString(), is("test"));
    }


    private void assertFileContents(MultipartFile file, MockMultipartFile expected) throws IOException {
        assertThat(IOUtils.toString(file.getInputStream(), "UTF-8"), is(IOUtils.toString(expected.getInputStream(), "UTF-8")));
    }
}
