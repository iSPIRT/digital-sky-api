package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import com.ispirit.digitalsky.helper.DigitalSignatureVerifierForTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.cert.X509Certificate;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class DigitalCertificateValidatorServiceImplTest {

    private X509Certificate clientCertificate;
    private String manufacturerCertificateChainPath;
    private DigitalCertificateValidatorServiceImpl digitalCertificateValidatorService;

    @Before
    public void setUp() {
        digitalCertificateValidatorService = new DigitalCertificateValidatorServiceImpl();
    }

    @Test
    public void shouldSuccessfullyValidateDigitalCertificate() throws Exception {
        clientCertificate = DigitalSignatureVerifierForTest.generateX509CertificateFromBase64EncodedString(DigitalSignatureVerifierForTest.getValidCertificateString());
        File resourcesDirectory = new File("");
        manufacturerCertificateChainPath = resourcesDirectory.getAbsolutePath() + "/final.pem";

        boolean isValid = digitalCertificateValidatorService.isValidCertificate(clientCertificate, manufacturerCertificateChainPath);

        assertTrue(isValid);
    }

    @Test
    public void shouldFailTheValidationIfTrustedCertificatePathValidationFails() throws Exception {
        clientCertificate = DigitalSignatureVerifierForTest.generateX509CertificateFromBase64EncodedString(DigitalSignatureVerifierForTest.getValidCertificateString());
        manufacturerCertificateChainPath = "/src/test/resources/invalidTrustedCertificateChain.pem";

        try {
            digitalCertificateValidatorService.isValidCertificate(clientCertificate, manufacturerCertificateChainPath);
            fail("should throw InvalidDigitalCertificateException");
        } catch(InvalidDigitalCertificateException e) {

        }
    }

    @Test
    public void shouldThrowInvalidDigitalCertificateExceptionIfTrustedCertificatePathIsInvalid() throws Exception {
        clientCertificate = DigitalSignatureVerifierForTest.generateX509CertificateFromBase64EncodedString(DigitalSignatureVerifierForTest.getValidCertificateString());
        manufacturerCertificateChainPath = "/src/test/resources/trustedCertificateChain.pem";

        try {
                digitalCertificateValidatorService.isValidCertificate(clientCertificate, manufacturerCertificateChainPath);
                fail("should throw InvalidDigitalCertificateException");
        } catch(InvalidDigitalCertificateException e) {

        }
    }

    @Test
    public void shouldThrowInvalidDigitalCertificateExceptionIfIssuerDNDoesNotMatch() throws Exception {
        clientCertificate = DigitalSignatureVerifierForTest.generateX509CertificateFromBase64EncodedString(DigitalSignatureVerifierForTest.getValidCertificateString());
        File resourcesDirectory = new File("src/test/resources");
        manufacturerCertificateChainPath = resourcesDirectory.getAbsolutePath() + "/fullCertificateChain.pem";

        try {
            digitalCertificateValidatorService.isValidCertificate(clientCertificate, manufacturerCertificateChainPath);
            fail("should throw InvalidDigitalCertificateException");
        } catch(InvalidDigitalCertificateException e) {

        }
    }


}
