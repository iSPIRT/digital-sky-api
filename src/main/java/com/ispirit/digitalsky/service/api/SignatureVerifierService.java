package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;

public interface SignatureVerifierService {
    boolean isValidSignature(RegisterDroneRequestPayload payload);
}
