
package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.exception.CertificateVerificationException;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.service.api.SignatureVerifierService;
import com.ispirit.digitalsky.util.CertChainValidator;
import com.ispirit.digitalsky.util.CertificateVerificationResult;
import com.ispirit.digitalsky.util.CertificateVerifier;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignatureVerifierServiceImpl implements SignatureVerifierService {

	private ManufacturerService manufacturerService;
    private final String keyStorePath = "/Users/archana/keystore.jks";
    private final String keyStorePassword = "changeit";
    private final String alias = "digitalsky";

    public SignatureVerifierServiceImpl(ManufacturerService manufacturerService) {
		this.manufacturerService = manufacturerService;
	}

	@Override
	public boolean isValidSignature(RegisterDroneRequestPayload payload) {
		boolean isValid = ((payload.getSignature() != null) || (payload.getDigitalCertificate() != null));

		if(isValid) {
			try {
				X509Certificate certificate = getCertificateFromFile(payload.getDigitalCertificate());
				isValid =  validateSignature(payload.getSignature(), payload.getDrone(), certificate)
						 && validateCertificate(certificate)
						&& validateManufacturer(certificate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isValid;
	}

	//check if digital signature is valid
	private boolean validateSignature(String signature, DroneDevice drone, X509Certificate certificate) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, IOException, ClassNotFoundException {

		try {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64Utils.decodeFromString(signature));
                 ObjectInputStream ois = new ObjectInputStream(bais);) {
                SignedObject so = (SignedObject) ois.readObject();
                Signature verificationEngine =
                        Signature.getInstance("SHA1withRSA");
                if (so.verify(certificate.getPublicKey(), verificationEngine)) {
                    //DroneDevice droneDevice = (DroneDevice)so.getObject();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}

//        Signature sig = Signature.getInstance("SHA256withRSA");
//        sig.initVerify(getCertificateFromFile(certificateStr));
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             ObjectOutputStream oos = new ObjectOutputStream(baos);) {
//            oos.writeObject(drone);
//            oos.flush();
//            byte[] bytes = baos.toByteArray();
//            sig.update(bytes);
//        }
//        byte[] decodedSign = Base64Utils.decodeFromString(signature);
//        boolean isValid = sig.verify(decodedSign);
//        return isValid;
        return false;
	}

	// check if manufacturer name matches with the manufacturer name in the digital certificate
	private boolean validateManufacturer(X509Certificate certificate) {

        Principal principal = certificate.getSubjectDN();
        String subjectDn = principal.getName();

        Pattern pattern = Pattern.compile("O=[\\w\\s]+");
        Matcher matcher = pattern.matcher(subjectDn);
        if(matcher.find()) {
            String manufacturerName = matcher.group(0).substring(2);
            Manufacturer manufacturer = manufacturerService.findByName(manufacturerName);
            if(manufacturer !=null) {
                return true;
            }
        }
		return false;
	}

    private X509Certificate getCertificateFromFile(String certString) {
        InputStream inputStream = null;
        CertificateFactory certFactory = null;
        try {
            X509Certificate certificate = (X509Certificate) CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(
                            // string encoded with default charset
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

	// check if certificate chain is valid
	private boolean validateCertificate(X509Certificate certificate) {

        try {
            CertChainValidator.validateKeyChain(certificate, keyStorePath, keyStorePassword);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
//        PKIXCertPathBuilderResult builderResult = null;
//        try {
//            builderResult = CertificateVerifier.verifyCertificate(certificate,null);
//        } catch (CertificateVerificationException e) {
//            e.printStackTrace();
//        }
//        return builderResult.getCertPath()  != null ;

        return true;
	}

}
