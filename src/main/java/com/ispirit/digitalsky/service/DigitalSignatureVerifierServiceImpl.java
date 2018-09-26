
package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.service.api.DigitalCertificateValidatorService;
import com.ispirit.digitalsky.service.api.ManufacturerService;
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

	private final ManufacturerService manufacturerService;
	private final String manufacturerAttributeNameInCertificate;
	private final DigitalCertificateValidatorService digitalCertificateValidatorService;
	private final boolean digitalCertificateValidationEnabled;

    public DigitalSignatureVerifierServiceImpl(ManufacturerService manufacturerService, DigitalCertificateValidatorService digitalCertificateValidatorService, String manufacturerAttributeNameInCertificate, boolean digitalCertificateValidationEnabled) {
		this.manufacturerService = manufacturerService;
		this.manufacturerAttributeNameInCertificate = manufacturerAttributeNameInCertificate;
		this.digitalCertificateValidatorService = digitalCertificateValidatorService;
		this.digitalCertificateValidationEnabled = digitalCertificateValidationEnabled;
	}

	@Override
	public boolean isValidSignature(RegisterDroneRequestPayload payload, long manufacturerId) {

        boolean isValid = ((payload.getSignature() != null) || (payload.getDigitalCertificate() != null));
        if(isValid) {
            String digitalCertificatePath = manufacturerService.getDigitalCertificatePath(manufacturerId);
            try {
                X509Certificate certificate = getCertificateFromFile(payload.getDigitalCertificate());
                isValid =  validateSignature(payload.getSignature(), payload.getDrone(), certificate)
                         && isValidCertificate(certificate, digitalCertificatePath)
                        && validateManufacturer(certificate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    private boolean validateSignature(String signature, DroneDevice drone, X509Certificate certificate) throws SignatureException {
        boolean isValid =  false;
        try {
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initVerify(certificate);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos);) {
                oos.writeObject(drone);
                oos.flush();
                rsa.update(baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            isValid = rsa.verify(Base64Utils.decodeFromString(signature));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    private boolean validateManufacturer(X509Certificate certificate) {
        Principal principal = certificate.getSubjectDN();
        String subjectDn = principal.getName();
        String attributeName = manufacturerAttributeNameInCertificate + "=";
        Pattern pattern = Pattern.compile(attributeName + "[\\w\\s\\.\\-]+",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subjectDn);
        if(matcher.find()) {
            String manufacturerName = matcher.group(0).substring(attributeName.length());
            Manufacturer manufacturer = manufacturerService.findByName(manufacturerName);
            if(manufacturer !=null) {
                return true;
            }
        }
		return false;
	}

    private X509Certificate getCertificateFromFile(String certString) {
        InputStream inputStream = null;
        try {
            X509Certificate certificate = (X509Certificate) CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(
                            new ByteArrayInputStream(Base64Utils.decode(certString.getBytes()))
                    );
            return certificate;
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
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
