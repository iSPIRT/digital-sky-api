package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;

public interface DigitalSignatureVerifierService {
    boolean isValidSignature(RegisterDroneRequestPayload payload, long manufacturerId);
}
