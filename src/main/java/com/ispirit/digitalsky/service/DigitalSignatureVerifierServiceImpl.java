
package com.ispirit.digitalsky.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import com.ispirit.digitalsky.exception.InvalidDigitalSignatureException;
import com.ispirit.digitalsky.exception.InvalidManufacturerException;
import com.ispirit.digitalsky.service.api.DigitalCertificateValidatorService;
import com.ispirit.digitalsky.service.api.DigitalSignatureVerifierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import java.io.*;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitalSignatureVerifierServiceImpl implements DigitalSignatureVerifierService {

	private final String manufacturerAttributeNameInCertificate;
	private final DigitalCertificateValidatorService digitalCertificateValidatorService;
	private final boolean digitalCertificateValidationEnabled;
    private ObjectMapper objectMapper;

    public DigitalSignatureVerifierServiceImpl(DigitalCertificateValidatorService digitalCertificateValidatorService,
                                               String manufacturerAttributeNameInCertificate,
                                               boolean digitalCertificateValidationEnabled, ObjectMapper objectMapper) {
		this.manufacturerAttributeNameInCertificate = manufacturerAttributeNameInCertificate;
		this.digitalCertificateValidatorService = digitalCertificateValidatorService;
		this.digitalCertificateValidationEnabled = digitalCertificateValidationEnabled;
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean isValidSignature(RegisterDroneRequestPayload payload, String orgName, String orgTrustedCertificatePath) throws InvalidDigitalCertificateException, InvalidManufacturerException, SignatureException {

        X509Certificate certificate = generateX509CertificateFromBase64EncodedString(payload.getDigitalCertificate());

        boolean isValid =  verifySignature(payload.getSignature(), payload.getDrone(), certificate)
                            && isValidCertificate(certificate, orgTrustedCertificatePath)
                            && verifyOrganizationInTheCertificate(certificate, orgName);

        return isValid;
    }

    private boolean verifySignature(String signature, DroneDevice drone, X509Certificate certificate) throws SignatureException, InvalidDigitalSignatureException {

        boolean isValid;
        try {
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initVerify(certificate.getPublicKey());
            try{
                rsa.update(objectMapper.writeValueAsString(drone).getBytes());
                isValid = rsa.verify(Base64Utils.decodeFromString(signature));
            } catch (IOException e) {
               throw new InvalidDigitalSignatureException();
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new InvalidDigitalCertificateException();
        }
        return isValid;
    }

    private boolean verifyOrganizationInTheCertificate(X509Certificate certificate, String orgName) throws InvalidDigitalCertificateException, InvalidManufacturerException {

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

    private X509Certificate generateX509CertificateFromBase64EncodedString(String certString) throws InvalidDigitalCertificateException{
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

        return this.digitalCertificateValidationEnabled ?
                digitalCertificateValidatorService.isValidCertificate(certificate, manufacturerCertificateChainPath)
                : true;
    }

}
