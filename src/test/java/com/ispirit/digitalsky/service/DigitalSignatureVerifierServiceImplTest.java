package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import com.ispirit.digitalsky.exception.InvalidManufacturerException;
import com.ispirit.digitalsky.helper.DigitalSignatureVerifierForTest;
import com.ispirit.digitalsky.service.api.DigitalCertificateValidatorService;
import org.junit.Before;
import org.junit.Test;


import java.security.SignatureException;
import java.security.cert.X509Certificate;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DigitalSignatureVerifierServiceImplTest {

    private DigitalCertificateValidatorService digitalCertificateValidatorService;
    private String manufacturerAttributeNameInCertificate;
    private boolean digitalCertificateValidationEnabled;
    private DigitalSignatureVerifierServiceImpl digitalSignatureVerifierService;

    @Before
    public void setUp()  {
        manufacturerAttributeNameInCertificate = "cn";
        digitalCertificateValidatorService = mock(DigitalCertificateValidatorService.class);
        digitalCertificateValidationEnabled = true;
        digitalSignatureVerifierService = new DigitalSignatureVerifierServiceImpl(digitalCertificateValidatorService, manufacturerAttributeNameInCertificate, digitalCertificateValidationEnabled);
    }

    @Test
    public void shouldThrowInvalidDigitalCertificateExceptionIfCertificateStringIsNotValid() throws SignatureException {

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(null, "", "");

        try {
            digitalSignatureVerifierService.isValidSignature(payload, "", "");
            fail("should throw InvalidDigitalCertificateException");
        } catch(InvalidDigitalCertificateException e) {

        }
    }

    @Test
    public void shouldThrowSignatureExceptionWhenSignatureIsInvalid() throws SignatureException {

        RegisterDroneRequestPayload payload = new RegisterDroneRequestPayload(DigitalSignatureVerifierForTest.getValidDroneDevice(), DigitalSignatureVerifierForTest.getValidSignatureString().replace("=","a"), DigitalSignatureVerifierForTest.getValidCertificateString());

        try {
            boolean val = digitalSignatureVerifierService.isValidSignature(payload, "", "");
            fail("should throw SignatureException");
        } catch(SignatureException e) {

        }
    }

    @Test
    public void shouldValidateCertificateIfValidationAttributeIsEnabled() throws SignatureException {

        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(DigitalSignatureVerifierForTest.getValidDroneDevice(), DigitalSignatureVerifierForTest.getValidSignatureString(), DigitalSignatureVerifierForTest.getValidCertificateString());
        when(digitalCertificateValidatorService.isValidCertificate(any(X509Certificate.class), anyString())).thenReturn(true);

        boolean val = digitalSignatureVerifierService.isValidSignature(requestPayload, "digitalsky-uat.centralindia.cloudapp.azure.com", "/src/resources/trustedCertificateChain.pem");

        verify(digitalCertificateValidatorService).isValidCertificate(any(X509Certificate.class), anyString());
    }

    @Test
    public void shouldThrowInvalidManufacturerException() throws SignatureException {
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(DigitalSignatureVerifierForTest.getValidDroneDevice(), DigitalSignatureVerifierForTest.getValidSignatureString(), DigitalSignatureVerifierForTest.getValidCertificateString());
        when(digitalCertificateValidatorService.isValidCertificate(any(X509Certificate.class), anyString())).thenReturn(true);

        try {
            boolean val = digitalSignatureVerifierService.isValidSignature(requestPayload, "digitalsky", "/src/resources/trustedCertificateChain.pem");

            fail("should throw InvalidManufacturerException");
        } catch (InvalidManufacturerException e) {

        }
    }

    @Test
    public void shouldValidateIfAllConditionsAreSatisfied() throws SignatureException {

        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(DigitalSignatureVerifierForTest.getValidDroneDevice(), DigitalSignatureVerifierForTest.getValidSignatureString(), DigitalSignatureVerifierForTest.getValidCertificateString());
        when(digitalCertificateValidatorService.isValidCertificate(any(X509Certificate.class), anyString())).thenReturn(true);

        boolean val = digitalSignatureVerifierService.isValidSignature(requestPayload, "digitalsky-uat.centralindia.cloudapp.azure.com", "/src/resources/trustedCertificateChain.pem");

        assertTrue(val);
        verify(digitalCertificateValidatorService).isValidCertificate(any(X509Certificate.class), anyString());
    }

    @Test
    public void shouldNotValidateCertificateIfValidationAttributeIsDisabled() throws SignatureException {
        digitalCertificateValidationEnabled = false;
        digitalSignatureVerifierService = new DigitalSignatureVerifierServiceImpl(digitalCertificateValidatorService, manufacturerAttributeNameInCertificate, digitalCertificateValidationEnabled);
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(DigitalSignatureVerifierForTest.getValidDroneDevice(), DigitalSignatureVerifierForTest.getValidSignatureString(), DigitalSignatureVerifierForTest.getValidCertificateString());
        when(digitalCertificateValidatorService.isValidCertificate(any(X509Certificate.class), anyString())).thenReturn(false);

        boolean val = digitalSignatureVerifierService.isValidSignature(requestPayload, "digitalsky-uat.centralindia.cloudapp.azure.com", "/src/resources/trustedCertificateChain.pem");

        verify(digitalCertificateValidatorService, never()).isValidCertificate(any(X509Certificate.class), anyString());
    }

    @Test
    public void shouldThrowInvalidDigitalCertificateExceptionIfCertificateDoesNotContainTheOrganizationAttribute() {
        digitalSignatureVerifierService = new DigitalSignatureVerifierServiceImpl(digitalCertificateValidatorService, "o", true);
        RegisterDroneRequestPayload requestPayload = new RegisterDroneRequestPayload(DigitalSignatureVerifierForTest.getValidDroneDevice(), DigitalSignatureVerifierForTest.getValidSignatureString(), DigitalSignatureVerifierForTest.getValidCertificateString());

        when(digitalCertificateValidatorService.isValidCertificate(any(X509Certificate.class), anyString())).thenReturn(true);

        try {
            boolean val = digitalSignatureVerifierService.isValidSignature(requestPayload, "digitalsky-uat.centralindia.cloudapp.azure.com", "/src/resources/trustedCertificateChain.pem");

            fail("should throw InvalidDigitalCertificateException");
        } catch(InvalidDigitalCertificateException e) {

        } catch (SignatureException e) {

        }
    }

}
