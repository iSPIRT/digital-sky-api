package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;

import java.security.cert.X509Certificate;

public interface DigitalCertificateValidatorService {
    boolean isValidCertificate(X509Certificate clientCertificate, String manufacturerCertificateChainPath) throws InvalidDigitalCertificateException;
}
