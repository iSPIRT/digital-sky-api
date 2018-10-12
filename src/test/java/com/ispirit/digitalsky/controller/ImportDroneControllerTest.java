package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.document.ImportDroneApplication;
import com.ispirit.digitalsky.document.UINApplication;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.service.api.DroneAcquisitionApplicationService;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.ispirit.digitalsky.SecurityContextHelper.setUserSecurityContext;
import static com.ispirit.digitalsky.controller.ImportDroneApplicationController.IMPORTDRONEACQUISITIONFORM_RESOURCE_BASE_PATH;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ImportDroneApplicationController.class, secure = false)
@Import(TestContext.class)
public class ImportDroneControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DroneAcquisitionApplicationService<ImportDroneApplication> applicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = setUserSecurityContext();
    }

    @Test
    public void shouldCreateApplication() throws Exception {

        //given
        ImportDroneApplication application = new ImportDroneApplication();

        //when
        MockHttpServletResponse response  =
                mvc.perform(post(IMPORTDRONEACQUISITIONFORM_RESOURCE_BASE_PATH)
                        .content(objectMapper.writeValueAsString(application))
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andReturn().getResponse();

        //then
        int status =  response.getStatus();
        assertThat(status, is(HttpStatus.CREATED.value()));
    }

    @Test
    public void shouldValidateUpdateApplication() throws Exception {

        InputStream securityClearanceDocContents = IOUtils.toInputStream("securityClearanceDocContents", "UTF-8");
        MockMultipartFile securityClearanceDoc = new MockMultipartFile("securityClearanceDoc", "securityClearanceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityClearanceDocContents);

        ImportDroneApplication application = new ImportDroneApplication();
        application.setStatus(ApplicationStatus.SUBMITTED);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(IMPORTDRONEACQUISITIONFORM_RESOURCE_BASE_PATH + "/1")
                .file(securityClearanceDoc)
                .param("droneAcquisitionForm", objectMapper.writeValueAsString(application));
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

        InputStream securityClearanceDocContents = IOUtils.toInputStream("securityClearanceDocContents", "UTF-8");
        MockMultipartFile securityClearanceDoc = new MockMultipartFile("securityClearanceDoc", "securityClearanceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityClearanceDocContents);

        ImportDroneApplication application = new ImportDroneApplication();
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setNoOfDrones(1);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(IMPORTDRONEACQUISITIONFORM_RESOURCE_BASE_PATH + "/1")
                .file(securityClearanceDoc)
                .param("droneAcquisitionForm", objectMapper.writeValueAsString(application));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();


        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        ArgumentCaptor<ImportDroneApplication> argumentCaptor = ArgumentCaptor.forClass(ImportDroneApplication.class);
        verify(applicationService).updateDroneAcquisitionApplication(eq("1"), argumentCaptor.capture(),eq(securityClearanceDoc));

    }

    @Test
    public void shouldHandleApplicationNotFoundExceptionWhileUpdatingApplication() throws Exception {

        InputStream securityClearanceDocContents = IOUtils.toInputStream("securityClearanceDocContents", "UTF-8");
        MockMultipartFile securityClearanceDoc = new MockMultipartFile("securityClearanceDoc", "securityClearanceDoc.txt", MediaType.MULTIPART_FORM_DATA_VALUE, securityClearanceDocContents);

        ImportDroneApplication application = new ImportDroneApplication();
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setNoOfDrones(1);

        MockHttpServletRequestBuilder requestBuilder = fileUpload(IMPORTDRONEACQUISITIONFORM_RESOURCE_BASE_PATH + "/1")
                .file(securityClearanceDoc)
                .param("droneAcquisitionForm", objectMapper.writeValueAsString(application));
        ReflectionTestUtils.setField(requestBuilder,"method","PATCH",String.class);

        //when
        MockHttpServletResponse response = mvc.perform(
                requestBuilder
        ).andReturn().getResponse();


        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        ArgumentCaptor<ImportDroneApplication> argumentCaptor = ArgumentCaptor.forClass(ImportDroneApplication.class);
        verify(applicationService).updateDroneAcquisitionApplication(eq("1"), argumentCaptor.capture(),eq(securityClearanceDoc));

    }
//
//    @Test
//    public void shouldApproveApplication() throws Exception {
//
//        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
//        approveRequestBody.setApplicationFormId("1");
//        approveRequestBody.setStatus(ApplicationStatus.APPROVED);
//
//        //when
//        MockHttpServletResponse response = mvc.perform(
//                patch(UIN_APPLICATION_RESOURCE_BASE_PATH + "/approve/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(approveRequestBody))
//        ).andReturn().getResponse();
//
//        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
//        ArgumentCaptor<ApproveRequestBody> argumentCaptor = ArgumentCaptor.forClass(ApproveRequestBody.class);
//        verify(uinApplicationService).approveApplication(argumentCaptor.capture());
//        assertThat(argumentCaptor.getValue().getApplicationFormId(), is(approveRequestBody.getApplicationFormId()));
//        assertThat(argumentCaptor.getValue().getStatus(), is(approveRequestBody.getStatus()));
//    }
//
//    @Test
//    public void shouldListApplicationsOfCurrentUser() throws Exception {
//
//        List<UINApplication> applications = asList(new UINApplication(), new UINApplication());
//        when(uinApplicationService.getApplicationsOfApplicant(userPrincipal.getId())).thenReturn(applications);
//
//        //when
//        MockHttpServletResponse response = mvc.perform(
//                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/list")
//        ).andReturn().getResponse();
//
//        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
//        List<UAOPApplication> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<UINApplication>>() {
//        });
//
//        assertThat(result.size(), is(2));
//    }
//
//    @Test
//    public void shouldGetAllApplicationsNotInDraftForAdmin() throws Exception {
//
//        UINApplication uinApplicationOne = new UINApplication();
//        uinApplicationOne.setStatus(ApplicationStatus.APPROVED);
//
//        UINApplication uinApplicationTwo = new UINApplication();
//        uinApplicationTwo.setStatus(ApplicationStatus.DRAFT);
//
//        UINApplication uinApplicationThree = new UINApplication();
//        uinApplicationThree.setStatus(ApplicationStatus.REJECTED);
//
//        UINApplication uinApplicationFour = new UINApplication();
//        uinApplicationFour.setStatus(ApplicationStatus.SUBMITTED);
//
//        List<UINApplication> applications = asList(uinApplicationOne, uinApplicationTwo, uinApplicationThree, uinApplicationFour);
//        when(uinApplicationService.getAllApplications()).thenReturn(applications);
//
//        //when
//        MockHttpServletResponse response = mvc.perform(
//                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/getAll")
//        ).andReturn().getResponse();
//
//        assertThat(response.getStatus(), is(HttpStatus. OK.value()));
//        List result = objectMapper.readValue(response.getContentAsString(), List.class);
//        assertThat(result.size(), is(3));
//    }
//
//    @Test
//    public void shouldNotGetApplicationIfUnAuthorizedUser() throws Exception {
//
//        UINApplication application = new UINApplication();
//        application.setApplicantId(userPrincipal.getId()+1);
//        when(uinApplicationService.get("1")).thenReturn(application);
//
//        //when
//        MockHttpServletResponse response = mvc.perform(
//                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1")
//        ).andReturn().getResponse();
//
//        assertThat(response.getStatus(), is(HttpStatus. UNAUTHORIZED.value()));
//    }
//
//    @Test
//    public void shouldNotGetFileIfUnAuthorizedUser() throws Exception {
//
//        UINApplication application = new UINApplication();
//        application.setApplicantId(userPrincipal.getId()+1);
//        when(uinApplicationService.get("1")).thenReturn(application);
//
//        //when
//        MockHttpServletResponse response = mvc.perform(
//                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
//        ).andReturn().getResponse();
//
//        verify(uinApplicationService, never()).getFile(anyString(), anyString());
//
//    }
//
//    @Test
//    public void shouldGetFile() throws Exception {
//
//        UINApplication application = new UINApplication();
//        application.setApplicantId(userPrincipal.getId());
//        when(uinApplicationService.get("1")).thenReturn(application);
//
//        when(uinApplicationService.getFile("1","sopDoc.txt")).thenReturn(new InMemoryResource("test"));
//
//        //when
//        MockHttpServletResponse response = mvc.perform(
//                get(UIN_APPLICATION_RESOURCE_BASE_PATH + "/1/document/sopDoc.txt")
//        ).andReturn().getResponse();
//
//        assertThat(response.getContentAsString(), is("test"));
//    }
//
//
    private void assertFileContents(MultipartFile file, MockMultipartFile expected) throws IOException {
        assertThat(IOUtils.toString(file.getInputStream(), "UTF-8"), is(IOUtils.toString(expected.getInputStream(), "UTF-8")));
    }

}
