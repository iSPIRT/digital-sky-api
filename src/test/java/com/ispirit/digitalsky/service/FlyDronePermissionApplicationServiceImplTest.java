package com.ispirit.digitalsky.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import com.ispirit.digitalsky.document.LatLong;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.domain.ApproveRequestBody;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.ApplicationNotFoundException;
import com.ispirit.digitalsky.exception.ApplicationNotInSubmittedStatus;
import com.ispirit.digitalsky.repository.FlyDronePermissionApplicationRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class FlyDronePermissionApplicationServiceImplTest {

    private FlyDronePermissionApplicationRepository repository;
    private StorageService storageService;
    private FlyDronePermissionApplicationServiceImpl service;
    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        repository = mock(FlyDronePermissionApplicationRepository.class);
        storageService = mock(StorageService.class);
        service = new FlyDronePermissionApplicationServiceImpl(repository, storageService);
        userPrincipal = SecurityContextHelper.setUserSecurityContext();

    }

    @Test
    public void shouldCreateApplication() throws Exception {
        //given
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setPilotId("1");
        application.setFlyArea(asList(new LatLong(1,1), new LatLong(2,2)));

        //when
        service.createApplication(application);

        //then
        ArgumentCaptor<FlyDronePermissionApplication> argumentCaptor = ArgumentCaptor.forClass(FlyDronePermissionApplication.class);
        verify(repository).insert(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCreatedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getLastModifiedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getApplicantId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getPilotId(), is(application.getPilotId()));
        assertThat(argumentCaptor.getValue().getFlyArea(), is(application.getFlyArea()));
    }

    @Test
    public void shouldSetSubmittedDateIfCreateApplicationSubmitted() throws Exception {
        //given
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setPilotId("1");
        application.setFlyArea(asList(new LatLong(1,1), new LatLong(2,2)));
        application.setStatus(ApplicationStatus.SUBMITTED);

        //when
        service.createApplication(application);

        //then
        ArgumentCaptor<FlyDronePermissionApplication> argumentCaptor = ArgumentCaptor.forClass(FlyDronePermissionApplication.class);
        verify(repository).insert(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getSubmittedDate(), notNullValue());
    }

    @Test
    public void shouldUpdateApplication() throws Exception {
        //given
        LocalDateTime dateTime = LocalDateTime.of(2018, Month.AUGUST, 12, 0, 0);
        FlyDronePermissionApplication applicationPayload = new FlyDronePermissionApplication();
        applicationPayload.setPilotId("2");
        applicationPayload.setFlyArea(asList(new LatLong(1,1), new LatLong(2,2)));
        applicationPayload.setStatus(ApplicationStatus.SUBMITTED);

        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setId("1");
        application.setPilotId("1");
        application.setFlyArea(asList(new LatLong(1,1), new LatLong(2,2)));
        application.setCreatedDate(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        application.setLastModifiedDate(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        application.setApplicantId(1);

        when(repository.findById("1")).thenReturn(application);

        //when
        service.updateApplication("1", applicationPayload);

        //then
        ArgumentCaptor<FlyDronePermissionApplication> argumentCaptor = ArgumentCaptor.forClass(FlyDronePermissionApplication.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCreatedDate(), is(application.getCreatedDate()));
        assertThat(argumentCaptor.getValue().getLastModifiedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getSubmittedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getApplicantId(), is(application.getApplicantId()));
        assertThat(argumentCaptor.getValue().getPilotId(), is(applicationPayload.getPilotId()));
        assertThat(argumentCaptor.getValue().getFlyArea(), is(applicationPayload.getFlyArea()));
    }

    @Test
    public void shouldThrowExceptionIfApplicationNotFoundDuringUpdate() throws Exception {

        //when
        try {
            service.updateApplication("1", new FlyDronePermissionApplication());
            fail("should have thrown ApplicationNotFoundException");
        } catch (ApplicationNotFoundException e) {

        }
    }

    @Test
    public void shouldMarkApplicationAsApproved() throws Exception {
        //given
        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId("1");
        approveRequestBody.setStatus(ApplicationStatus.APPROVED);
        approveRequestBody.setComments("comments");

        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setId("1");
        application.setPilotId("1");
        application.setFlyArea(asList(new LatLong(1,1), new LatLong(2,2)));
        application.setApplicantId(1);
        application.setStatus(ApplicationStatus.SUBMITTED);
        when(repository.findById("1")).thenReturn(application);

        //when
        service.approveApplication(approveRequestBody);

        //then
        ArgumentCaptor<FlyDronePermissionApplication> argumentCaptor = ArgumentCaptor.forClass(FlyDronePermissionApplication.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getApprover(), is(userPrincipal.getUsername()));
        assertThat(argumentCaptor.getValue().getApproverId(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getApprovedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getApproverComments(), is(approveRequestBody.getComments()));
        assertThat(argumentCaptor.getValue().getStatus(), is(ApplicationStatus.APPROVED));
    }

    @Test
    public void shouldThrowExceptionIfApplicationNotFoundDuringApprove() throws Exception {
        //given
        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId("1");
        approveRequestBody.setStatus(ApplicationStatus.APPROVED);
        approveRequestBody.setComments("comments");
        //when
        try {
            service.approveApplication(approveRequestBody);
            fail("should have thrown ApplicationNotFoundException");
        } catch (ApplicationNotFoundException e) {

        }
    }

    @Test
    public void shouldNotApproveApplicationIfNotSubmitted() throws Exception {
        //given
        ApproveRequestBody approveRequestBody = new ApproveRequestBody();
        approveRequestBody.setApplicationFormId("1");
        approveRequestBody.setStatus(ApplicationStatus.APPROVED);
        approveRequestBody.setComments("comments");

        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setStatus(ApplicationStatus.DRAFT);
        when(repository.findById("1")).thenReturn(application);

        //when
        try {
            service.approveApplication(approveRequestBody);
            fail("should have thrown ApplicationNotFoundException");
        } catch (ApplicationNotInSubmittedStatus e) {

        }
    }

    @Test
    public void shouldGetApplicationById() throws Exception {
        //given
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        when(repository.findById("1")).thenReturn(application);

        //when
        FlyDronePermissionApplication result = service.get("1");

        //then
        verify(repository).findById("1");
        assertThat(result, is(application));
    }

    @Test
    public void shouldGetAllApplicationsForGivenDrone() throws Exception {
        FlyDronePermissionApplication applicationOne = application(LocalDateTime.of(2018, Month.AUGUST, 29, 0, 0, 0));
        FlyDronePermissionApplication applicationTwo = application(LocalDateTime.of(2018, Month.AUGUST, 30, 0, 0, 0));
        FlyDronePermissionApplication applicationThree = application(LocalDateTime.of(2018, Month.AUGUST, 31, 0, 0, 0));

        when(repository.findByDroneId(1L)).thenReturn(asList(applicationOne, applicationTwo, applicationThree));

        //when
        Collection<FlyDronePermissionApplication> applications = service.getApplicationsOfDrone(1L);

        assertThat(applications, is(asList(applicationThree, applicationTwo, applicationOne)));

    }

    @Test
    public void shouldGetAllApplications() throws Exception {
        FlyDronePermissionApplication applicationOne = application(LocalDateTime.of(2018, Month.AUGUST, 29, 0, 0, 0));
        FlyDronePermissionApplication applicationTwo = application(LocalDateTime.of(2018, Month.AUGUST, 30, 0, 0, 0));
        FlyDronePermissionApplication applicationThree = application(LocalDateTime.of(2018, Month.AUGUST, 31, 0, 0, 0));

        when(repository.findAll()).thenReturn(asList(applicationOne, applicationTwo, applicationThree));

        //when
        Collection<FlyDronePermissionApplication> applications = service.getAllApplications();

        assertThat(applications, is(asList(applicationThree, applicationTwo, applicationOne)));

    }

    @Test
    public void shouldGetFile() throws Exception {

        //when
        service.getFile("1","test.txt");

        //then
        verify(storageService).loadAsResource("1","test.txt");
    }

    @Test
    public void name() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setFlyArea(asList(new LatLong(1,2), new LatLong(2,2)));
        System.out.println(objectMapper.writeValueAsString(application));
        String a = "{\"id\":\"5b9613fad46b62254cbc45d7\",\"createdDate\":\"10-09-2018\",\"applicationNumber\":null,\"applicant\":\"praveen\",\"applicantId\":2,\"applicantAddress\":null,\"applicantEmail\":null,\"applicantPhone\":null,\"applicantNationality\":null,\"applicantType\":null,\"submittedDate\":null,\"lastModifiedDate\":\"10-09-2018\",\"status\":\"DRAFT\",\"approver\":null,\"approverId\":0,\"approvedDate\":null,\"approverComments\":null,\"pilotId\":\"sample\",\"flyArea\":[[1.1,1.1],[2.1,2.1]],\"droneId\":1}";
        FlyDronePermissionApplication value = objectMapper.readValue(a, FlyDronePermissionApplication.class);
        System.out.println(value);
    }

    private FlyDronePermissionApplication application(LocalDateTime dateTime) {
        FlyDronePermissionApplication application = new FlyDronePermissionApplication();
        application.setLastModifiedDate(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
        return application;
    }
}