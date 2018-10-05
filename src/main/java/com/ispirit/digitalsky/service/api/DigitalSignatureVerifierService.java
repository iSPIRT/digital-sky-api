package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import com.ispirit.digitalsky.exception.InvalidManufacturerException;

import java.security.SignatureException;

public interface DigitalSignatureVerifierService {
    boolean isValidSignature(RegisterDroneRequestPayload payload, String orgName, String orgTrustedCertificatePath) throws InvalidDigitalCertificateException, InvalidManufacturerException, SignatureException ;
}
