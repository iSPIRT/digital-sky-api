
package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.service.api.SignatureVerifierService;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SignatureVerifierServiceImpl implements SignatureVerifierService {

	private ManufacturerService manufacturerService;

	public SignatureVerifierServiceImpl(ManufacturerService manufacturerService) {
		this.manufacturerService = manufacturerService;
	}

	@Override
	public boolean isValidSignature(RegisterDroneRequestPayload payload) {
		boolean isValid = ((payload.getSignature() != null) || (payload.getDigitalCertificate() != null));

//		if(isValid) {
//			try {
//				X509Certificate certificate = getCertificateFromFile(payload.getDigitalCertificate());
//				isValid =  validateSignature(payload.getSignature(), payload.getDrone(), certificate)
//						&& validateCertificate(certificate)
//						&& validateManufacturer(certificate);
//			} catch (GeneralSecurityException e) {
//				e.printStackTrace();
//			}
//		}

		return isValid;
	}

	//check if digital signature is valid
	private boolean validateSignature(String signature, DroneDevice drone, X509Certificate certificate) throws SignatureException {

		boolean isValid =  false;

		try {
			Signature rsa = Signature.getInstance("SHA1withRSA");
			rsa.initVerify(certificate);
			rsa.update(SerializationUtils.serialize(drone));
			isValid = rsa.verify(signature.getBytes());
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}

		return isValid;
	}

	// check if manufacturer name matches with the manufacturer name in the digital certificate
	private boolean validateCertificate(X509Certificate certificate) {

		String manufacturerName = certificate.getIssuerX500Principal().getName();
		Manufacturer manufacturer = manufacturerService.findByName(manufacturerName);

		return ((manufacturer != null) ? true : false);
	}

	// check if certificate chain is valid
	private boolean validateManufacturer(X509Certificate certificate) {
		return true;
	}

	private X509Certificate getCertificateFromFile(String certificateFile) {
        InputStream inputStream = null;
        CertificateFactory certFactory = null;
        try {
            certFactory = CertificateFactory.getInstance("X.509");
            inputStream = new ByteArrayInputStream(certificateFile.getBytes(Charset.forName("UTF-8")));
            X509Certificate certificate =  (X509Certificate) certFactory.generateCertificate(inputStream);
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

}
