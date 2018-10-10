package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.document.UAOPApplication;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.ApplicationNotFoundException;
import com.ispirit.digitalsky.exception.ApplicationNotInSubmittedStatusException;
import com.ispirit.digitalsky.exception.UnAuthorizedAccessException;
import com.ispirit.digitalsky.repository.UAOPApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UAOPApplicationServiceImplTest {

    private UAOPApplicationRepository repository;
    private StorageService storageService;
    private UAOPApplicationServiceImpl service;
    private UserPrincipal userPrincipal;
    private String fileName;

    @Before
    public void setUp() throws Exception {
        repository = mock(UAOPApplicationRepository.class);
        storageService = mock(StorageService.class);
        service = new UAOPApplicationServiceImpl(repository, storageService);
        userPrincipal = SecurityContextHelper.setUserSecurityContext();
    }

    @Test
    public void shouldCreateApplication() throws Exception {
        //given
        UAOPApplication uaopApplication = new UAOPApplication();
        when(repository.insert(uaopApplication)).thenReturn(uaopApplication);

        //when
        service.createApplication(uaopApplication);

        //then
        ArgumentCaptor<UAOPApplication> argumentCaptor = ArgumentCaptor.forClass(UAOPApplication.class);
        verify(repository).insert(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getApplicantId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getApplicant(), is(userPrincipal.getUsername()));
    }

    @Test
    public void shouldSetSubmittedDateForCreateApplication() throws Exception {
        //given
        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setStatus(ApplicationStatus.SUBMITTED);
        when(repository.insert(uaopApplication)).thenReturn(uaopApplication);

        //when
        service.createApplication(uaopApplication);

        //then
        ArgumentCaptor<UAOPApplication> argumentCaptor = ArgumentCaptor.forClass(UAOPApplication.class);
        verify(repository).insert(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getSubmittedDate(), notNullValue());
    }

    @Test
    public void shouldSaveDocsForCreateApplication() throws Exception {
        //given
        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setId("id");
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("lop", "content1".getBytes());
        MockMultipartFile sopDoc = new MockMultipartFile("sop", "content2".getBytes());
        uaopApplication.setLandOwnerPermissionDoc(landOwnerPermissionDoc);
        uaopApplication.setSopDoc(sopDoc);
        when(repository.insert(uaopApplication)).thenReturn(uaopApplication);

        //when
        service.createApplication(uaopApplication);

        //then
        verify(storageService).store(asList(sopDoc, landOwnerPermissionDoc), uaopApplication.getId());
    }

    @Test
    public void shouldUpdateApplication() throws Exception {
        //given
        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setName("name");
        uaopApplication.setDesignation("designation");
        uaopApplication.setStatus(ApplicationStatus.SUBMITTED);
        MockMultipartFile landOwnerPermissionDoc = new MockMultipartFile("lop", "content1".getBytes());
        MockMultipartFile sopDoc = new MockMultipartFile("sop", "content2".getBytes());
        uaopApplication.setLandOwnerPermissionDoc(landOwnerPermissionDoc);
        uaopApplication.setSopDoc(sopDoc);

        UAOPApplication currentApplication = new UAOPApplication();
        when(repository.findById("id")).thenReturn(currentApplication);
        when(repository.save(currentApplication)).thenReturn(currentApplication);

        //when
        service.updateApplication("id", uaopApplication);

        //then
        ArgumentCaptor<UAOPApplication> argumentCaptor = ArgumentCaptor.forClass(UAOPApplication.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getSubmittedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getName(), is(uaopApplication.getName()));
        assertThat(argumentCaptor.getValue().getDesignation(), is(uaopApplication.getDesignation()));
        assertThat(argumentCaptor.getValue().getStatus(), is(uaopApplication.getStatus()));
        verify(storageService).store(asList(sopDoc, landOwnerPermissionDoc), uaopApplication.getId());

    }

    @Test
    public void shouldThrowExceptionIfApplicationNotFoundForApproval() throws Exception {
        //given
        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId("id");
        when(repository.findById(approveRequestBody.getApplicationFormId())).thenReturn(null);

        //when
        try {
            service.approveApplication(approveRequestBody);
            fail("should have thrown ApplicationNotFoundException");
        } catch (ApplicationNotFoundException e) {
        }
    }

    @Test
    public void shouldThrowExceptionIfApplicationAlreadySubmittedForApproval() throws Exception {
        //given
        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId("id");
        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setStatus(ApplicationStatus.DRAFT);
        when(repository.findById(approveRequestBody.getApplicationFormId())).thenReturn(uaopApplication);

        //when
        try {
            service.approveApplication(approveRequestBody);
            fail("should have thrown ApplicationNotFoundException");
        } catch (ApplicationNotInSubmittedStatusException e) {
        }
    }

    @Test
    public void shouldApproveApplication() throws Exception {
        //given
        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setStatus(ApplicationStatus.APPROVED);
        approveRequestBody.setApplicationFormId("id");
        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setStatus(ApplicationStatus.SUBMITTED);
        when(repository.findById(approveRequestBody.getApplicationFormId())).thenReturn(uaopApplication);

        //when
        service.approveApplication(approveRequestBody);

        //then
        ArgumentCaptor<UAOPApplication> argumentCaptor = ArgumentCaptor.forClass(UAOPApplication.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus(), is(ApplicationStatus.APPROVED));
        assertThat(argumentCaptor.getValue().getApprovedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getApproverId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getApprover(), is(userPrincipal.getUsername()));
    }

    @Test
    public void shouldGetApplicationById() throws Exception {
        //given
        String id = "id";
        UAOPApplication uaopApplication = new UAOPApplication();
        when(repository.findById(id)).thenReturn(uaopApplication);

        //when
        UAOPApplication result = service.get(id);

        //then
        verify(repository).findById(id);
        assertThat(result, is(uaopApplication));
    }

    @Test
    public void shouldGetApplicationsByApplicantId() throws Exception {
        //given
        long id = 1L;
        UAOPApplication uaopApplication = new UAOPApplication();
        when(repository.findByApplicantId(id)).thenReturn(asList(uaopApplication));

        //when
        Collection<UAOPApplication> result = service.getApplicationsOfApplicant(id);

        //then
        assertThat(result, is(asList(uaopApplication)));
    }


    @Test
    public void shouldGetAllApplications() throws Exception {
        //given
        UAOPApplication uaopApplication = new UAOPApplication();
        when(repository.findAll()).thenReturn(asList(uaopApplication));

        //when
        Collection<UAOPApplication> result = service.getAllApplications();

        //then
        assertThat(result, is(asList(uaopApplication)));
    }

    @Test
    public void shouldGetFileForApplications() throws Exception {
        //given
        String id = "id";
        fileName = "fileName";

        //when
        service.getFile(id, fileName);

        //then
        verify(storageService).loadAsResource(id, fileName);
    }
}