package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;

import java.security.SignatureException;

public interface DigitalSignatureVerifierService {
    boolean isValidSignature(RegisterDroneRequestPayload payload, String orgName, String orgTrustedCertificatePath) throws SignatureException;
}
