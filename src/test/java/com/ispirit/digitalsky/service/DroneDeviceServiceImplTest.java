package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.exception.*;
import com.ispirit.digitalsky.repository.DroneDeviceRepository;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.DigitalSignatureVerifierService;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.service.api.OperatorDroneService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.security.SignatureException;
import java.time.LocalDate;
import java.util.Collection;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DroneDeviceServiceImplTest {

    private DroneDeviceRepository droneDeviceRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private OperatorDroneService operatorDroneService;
    private DigitalSignatureVerifierService signatureVerifierService;
    private ManufacturerService manufacturerService;
    private UserPrincipal userPrincipal;

    private DroneDeviceServiceImpl droneDeviceService;

    @Before
    public void setUp()  {

        droneDeviceRepository = mock(DroneDeviceRepository.class);
        individualOperatorRepository = mock(IndividualOperatorRepository.class);
        organizationOperatorRepository = mock(OrganizationOperatorRepository.class);
        operatorDroneService = mock(OperatorDroneService.class);
        signatureVerifierService = mock(DigitalSignatureVerifierService.class);
        manufacturerService = mock(ManufacturerService.class);

        userPrincipal = SecurityContextHelper.setUserSecurityContext();

        droneDeviceService = new DroneDeviceServiceImpl(droneDeviceRepository, signatureVerifierService, individualOperatorRepository, organizationOperatorRepository, operatorDroneService, manufacturerService);
    }

    @Test
    public void shouldThrowManufacturerNotFoundExceptionForRegisterDrone() {
        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(null);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(null);

        try {
            DroneDevice droneDevice = droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", null);
            fail("should have thrown ManufacturerNotFoundException");
        } catch(ManufacturerNotFoundException e) {

        }

        verifyZeroInteractions(signatureVerifierService);
        verifyZeroInteractions(droneDeviceRepository);
        verify(manufacturerService, never()).getCAAndTrustedCertificatePath(anyLong());
    }

    @Test
    public void shouldThrowManufacturerDigitalCertificateNotFoundExceptionForRegisterDrone() {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        doThrow(new ManufacturerTrustedCertificateNotFoundException()).when(manufacturerService).getCAAndTrustedCertificatePath(anyLong());

        try {
            DroneDevice droneDevice = droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", null);
            fail("should have thrown ManufacturerTrustedCertificateNotFoundException");
        } catch(ManufacturerTrustedCertificateNotFoundException e) {

        }

        verifyZeroInteractions(signatureVerifierService);
        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowInvalidDigitalSignatureExceptionForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(false);

        try {
            droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidDigitalSignatureException");
        } catch(InvalidDigitalSignatureException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);

    }

    @Test
    public void shouldThrowInvalidDigitalSignatureExceptionFromSignatureServiceForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        doThrow(new SignatureException()).when(signatureVerifierService).isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath));

        try {
            droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidDigitalSignatureException");
        } catch(InvalidDigitalSignatureException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowInvalidManufacturerExceptionFromSignatureServiceForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        doThrow(new InvalidManufacturerException()).when(signatureVerifierService).isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath));

        try {
            droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidManufacturerException");
        } catch(InvalidManufacturerException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowInvalidInvalidDigitalCertificateExceptionFromSignatureServiceForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        doThrow(new InvalidDigitalCertificateException()).when(signatureVerifierService).isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath));

        try {
            droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidDigitalCertificateException");
        } catch(InvalidDigitalCertificateException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowDroneDeviceAlreadyExistExceptionForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);
        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(new DroneDevice() );
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(new DroneDevice("beebopeff292929","","","","",""),"","");
        try {
            droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", payload);
            fail("should throw DroneDeviceAlreadyExistException");
        } catch(DroneDeviceAlreadyExistException e) {

        }

        verify(droneDeviceRepository, never()).save(any(DroneDevice.class));
    }

    @Test
    public void shouldThrowOperatorBusinessIdentifierMissingExceptionForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);
        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(null);
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(new DroneDevice("beebopeff292929","","","","",null),"","");
        try {
            droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", payload);
            fail("should throw OperatorBusinessIdentifierMissingException");
        } catch(OperatorBusinessIdentifierMissingException e) {

        }

        verify(droneDeviceRepository, never()).save(any(DroneDevice.class));
    }

    @Test
    public void shouldThrowInvalidOperatorBusinessIdentifierExceptionForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);
        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(null);
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);
        when(organizationOperatorRepository.loadByBusinessIdentifier(eq("2ff217e740534fde89c1bfe62e08f316"))).thenReturn(null);
        when(individualOperatorRepository.loadByBusinessIdentifier(eq("2ff217e740534fde89c1bfe62e08f316"))).thenReturn(null);

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(new DroneDevice("beebopeff292929","","","","","2ff217e740534fde89c1bfe62e08f316"),"","");
        try {
            droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", payload);
            fail("should throw InvalidOperatorBusinessIdentifierException");
        } catch(InvalidOperatorBusinessIdentifierException e) {

        }

        verify(droneDeviceRepository, never()).save(any(DroneDevice.class));
    }

    @Test
    public void shouldFetchOrganizationOperatorBusinessIdentiferForRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);
        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(null);
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);
        when(organizationOperatorRepository.loadByBusinessIdentifier(eq("2ff217e740534fde89c1bfe62e08f316"))).thenReturn(new OrganizationOperator(1,"","","","","","",null));
        when(individualOperatorRepository.loadByBusinessIdentifier(eq("2ff217e740534fde89c1bfe62e08f316"))).thenReturn(null);

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(new DroneDevice("beebopeff292929","","","","","2ff217e740534fde89c1bfe62e08f316"),"","");

        droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", payload);

        verify(organizationOperatorRepository, atLeastOnce()).loadByBusinessIdentifier(eq("2ff217e740534fde89c1bfe62e08f316"));
    }

    @Test
    public void shouldRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);
        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(null);
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);
        when(organizationOperatorRepository.loadByBusinessIdentifier(eq("2ff217e740534fde89c1bfe62e08f316"))).thenReturn(new OrganizationOperator(1,"","","","","","",null));
        when(individualOperatorRepository.loadByBusinessIdentifier(eq("2ff217e740534fde89c1bfe62e08f316"))).thenReturn(null);
        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(new DroneDevice("beebopeff292929","","","","","2ff217e740534fde89c1bfe62e08f316"),"","");

        droneDeviceService.register("eff217e740534fde89c1bfe62e08f316", payload);

        //then
        ArgumentCaptor<DroneDevice> argumentCaptor = ArgumentCaptor.forClass(DroneDevice.class);
        verify(droneDeviceRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCreatedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getManufacturerBusinessIdentifier(), is("eff217e740534fde89c1bfe62e08f316"));
        assertThat(argumentCaptor.getValue().getRegistrationStatus(), is(DroneDeviceRegistrationStatus.REGISTERED));

    }

    @Test
    public void shouldThrowManufacturerNotFoundExceptionForDeRegisterDrone() {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        doThrow(new ManufacturerTrustedCertificateNotFoundException()).when(manufacturerService).getCAAndTrustedCertificatePath(anyLong());

        try {
            DroneDevice droneDevice = droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", null);
            fail("should have thrown ManufacturerTrustedCertificateNotFoundException");
        } catch(ManufacturerTrustedCertificateNotFoundException e) {

        }

        verifyZeroInteractions(signatureVerifierService);
        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowManufacturerDigitalCertificateNotFoundExceptionForDeRegisterDrone() {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        doThrow(new ManufacturerTrustedCertificateNotFoundException()).when(manufacturerService).getCAAndTrustedCertificatePath(anyLong());

        try {
            DroneDevice droneDevice = droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", null);
            fail("should have thrown ManufacturerTrustedCertificateNotFoundException");
        } catch(ManufacturerTrustedCertificateNotFoundException e) {

        }

        verifyZeroInteractions(signatureVerifierService);
        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowInvalidDigitalSignatureExceptionForDeRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(false);

        try {
            droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidDigitalSignatureException");
        } catch(InvalidDigitalSignatureException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowInvalidDigitalSignatureExceptionFromSignatureServiceForDeRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        doThrow(new SignatureException()).when(signatureVerifierService).isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath));

        try {
            droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidDigitalSignatureException");
        } catch(InvalidDigitalSignatureException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowInvalidManufacturerExceptionFromSignatureServiceForDeRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        doThrow(new InvalidManufacturerException()).when(signatureVerifierService).isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath));

        try {
            droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidManufacturerException");
        } catch(InvalidManufacturerException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowInvalidInvalidDigitalCertificateExceptionFromSignatureServiceForDeRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        doThrow(new InvalidDigitalCertificateException()).when(signatureVerifierService).isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath));

        try {
            droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", new RegisterDroneRequestPayload());
            fail("should throw InvalidDigitalCertificateException");
        } catch(InvalidDigitalCertificateException e) {

        }

        verifyZeroInteractions(droneDeviceRepository);
    }

    @Test
    public void shouldThrowDroneDeviceNotFoundExceptionForDeRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);
        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(null);
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(new DroneDevice("beebopeff292929","","","","",""),"","");
        try {
            droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", payload);
            fail("should throw DroneDeviceNotFoundException");
        } catch(DroneDeviceNotFoundException e) {

        }

        verify(droneDeviceRepository, never()).save(any(DroneDevice.class));
    }

    @Test
    public void shouldThrowDeviceNotInRegisteredStateExceptionForDeRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";
        DroneDevice droneDevice=  new DroneDevice();
        droneDevice.setRegistrationStatus(DroneDeviceRegistrationStatus.DEREGISTERED);

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);

        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(droneDevice);
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(new DroneDevice("beebopeff292929","","","","",""),"","");
        try {
            droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", payload);
            fail("should throw DeviceNotInRegisteredStateException");
        } catch(DeviceNotInRegisteredStateException e) {

        }

        verify(droneDeviceRepository, never()).save(any(DroneDevice.class));
    }

    @Test
    public void shouldDeRegisterDrone() throws SignatureException {
        Manufacturer manufacturer = new Manufacturer(1,null,null,null,null,null,null, null);
        String certificatePath = "/Users/trustedCertificateChain.pem";
        DroneDevice droneDevice =new DroneDevice("beebopeff292929","","","","","2ff217e740534fde89c1bfe62e08f316");
        droneDevice.setRegistrationStatus(DroneDeviceRegistrationStatus.REGISTERED);

        when(manufacturerService.loadByBusinessIdentifier(eq("eff217e740534fde89c1bfe62e08f316"))).thenReturn(manufacturer);
        when(manufacturerService.getCAAndTrustedCertificatePath(anyLong())).thenReturn(certificatePath);
        when(signatureVerifierService.isValidSignature(any(RegisterDroneRequestPayload.class),anyString(),eq(certificatePath))).thenReturn(true);
        when(droneDeviceRepository.findByDeviceId("beebopeff292929")).thenReturn(droneDevice);
        when(droneDeviceRepository.save(any(DroneDevice.class))).thenReturn(null);
        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(droneDevice,"","");
        payload.getDrone().setManufacturerBusinessIdentifier("eff217e740534fde89c1bfe62e08f316");
        payload.getDrone().setCreatedDate(LocalDate.of(2010,1,1));

        droneDeviceService.deregister("eff217e740534fde89c1bfe62e08f316", payload);

        //then
        ArgumentCaptor<DroneDevice> argumentCaptor = ArgumentCaptor.forClass(DroneDevice.class);
        verify(droneDeviceRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getLastModifiedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getOperatorBusinessIdentifier(), is("2ff217e740534fde89c1bfe62e08f316"));
        assertThat(argumentCaptor.getValue().getManufacturerBusinessIdentifier(), is("eff217e740534fde89c1bfe62e08f316"));
        assertThat(argumentCaptor.getValue().getCreatedDate(), is(LocalDate.of(2010,1,1)));
        assertThat(argumentCaptor.getValue().getRegistrationStatus(), is(DroneDeviceRegistrationStatus.DEREGISTERED));
    }

    @Test
    public void shouldGetRegisteredDroneDeviceIds() {

        when(droneDeviceRepository.findRegisteredDroneDeviceIds("2ff217e740534fde89c1bfe62e08f316")).thenReturn(asList("Beebop199ef45", "ppv27787efa"));
        when(operatorDroneService.getAvailableDroneDeviceIds(asList("Beebop199ef45","ppv27787efa"))).thenReturn(asList("Beebop199ef45"));

        Collection<?> deviceIds =  droneDeviceService.getRegisteredDroneDeviceIds("2ff217e740534fde89c1bfe62e08f316");

        verify(droneDeviceRepository).findRegisteredDroneDeviceIds("2ff217e740534fde89c1bfe62e08f316");
        verify(operatorDroneService).getAvailableDroneDeviceIds(asList("Beebop199ef45","ppv27787efa"));
        assertEquals (deviceIds.toArray()[0], "Beebop199ef45");
    }

}
