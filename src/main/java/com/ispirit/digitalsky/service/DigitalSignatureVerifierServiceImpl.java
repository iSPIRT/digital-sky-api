
package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import com.ispirit.digitalsky.exception.InvalidDigitalSignatureException;
import com.ispirit.digitalsky.exception.InvalidManufacturerException;
import com.ispirit.digitalsky.service.api.DigitalCertificateValidatorService;
import com.ispirit.digitalsky.service.api.DigitalSignatureVerifierService;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitalSignatureVerifierServiceImpl implements DigitalSignatureVerifierService {

	private final String manufacturerAttributeNameInCertificate;
	private final DigitalCertificateValidatorService digitalCertificateValidatorService;
	private final boolean digitalCertificateValidationEnabled;

    public DigitalSignatureVerifierServiceImpl(DigitalCertificateValidatorService digitalCertificateValidatorService,
                                               String manufacturerAttributeNameInCertificate,
                                               boolean digitalCertificateValidationEnabled) {
		this.manufacturerAttributeNameInCertificate = manufacturerAttributeNameInCertificate;
		this.digitalCertificateValidatorService = digitalCertificateValidatorService;
		this.digitalCertificateValidationEnabled = digitalCertificateValidationEnabled;
	}

	@Override
	public boolean isValidSignature(RegisterDroneRequestPayload payload, String orgName, String orgTrustedCertificatePath) throws InvalidDigitalCertificateException, InvalidManufacturerException, SignatureException {

        boolean isValid = ((payload.getSignature() != null) || (payload.getDigitalCertificate() != null));
        if(isValid) {
                X509Certificate certificate = getCertificateFromFile(payload.getDigitalCertificate());
                isValid =  validateSignature(payload.getSignature(), payload.getDrone(), certificate)
                         && isValidCertificate(certificate, orgTrustedCertificatePath)
                        && validateOrganization(certificate, orgName);
        }

        return isValid;
    }

    private boolean validateSignature(String signature, DroneDevice drone, X509Certificate certificate) throws SignatureException, InvalidDigitalSignatureException {
        boolean isValid;
        try {
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initVerify(certificate);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);) {
                oos.writeObject(drone);
                oos.flush();
                rsa.update(baos.toByteArray());
                isValid = rsa.verify(Base64Utils.decodeFromString(signature));
            } catch (IOException e) {
               throw new InvalidDigitalSignatureException();
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new InvalidDigitalCertificateException();
        }
        return isValid;
    }

    private boolean validateOrganization(X509Certificate certificate, String orgName) throws InvalidDigitalCertificateException, InvalidManufacturerException {
        Principal principal = certificate.getSubjectDN();
        String subjectDn = principal.getName();
        String attributeName = manufacturerAttributeNameInCertificate + "=";
        Pattern pattern = Pattern.compile(attributeName + "[\\w\\s\\.\\-]+",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subjectDn);
        if(!matcher.find()) { throw new InvalidDigitalCertificateException(); }

        String manufacturerOrgName = matcher.group(0).substring(attributeName.length());
        if(!manufacturerOrgName.equals(orgName)) { throw new InvalidManufacturerException(); }
        else { return true; }

	}

    private X509Certificate getCertificateFromFile(String certString) throws InvalidDigitalCertificateException{
        InputStream inputStream = null;
        try {
            X509Certificate certificate = (X509Certificate) CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(
                            new ByteArrayInputStream(Base64Utils.decode(certString.getBytes()))
                    );
            return certificate;
        } catch (CertificateException e) {
           throw new InvalidDigitalCertificateException();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new InvalidDigitalCertificateException();
                }
            }
        }
    }

	private boolean isValidCertificate(X509Certificate certificate, String manufacturerCertificateChainPath) {
        if(this.digitalCertificateValidationEnabled) {
            Set<X509Certificate> certificates = new HashSet<>();
            certificates.add(certificate);
            boolean isValid = digitalCertificateValidatorService.isValidCertificate(certificate, manufacturerCertificateChainPath);
            return isValid;
        } else {
            return true;
        }
    }

}
