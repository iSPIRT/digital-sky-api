package com.ispirit.digitalsky.util;

import java.security.cert.PKIXCertPathBuilderResult;

public class CertificateVerificationResult {
    private boolean valid;
    private PKIXCertPathBuilderResult result;
    private Throwable exception;

    /**
     * Constructs a certificate verification result for valid
     * certificate by given certification path.
     */
    public CertificateVerificationResult(
            PKIXCertPathBuilderResult result) {
        this.valid = true;
        this.result = result;
    }

    /**
     * Constructs a certificate verification result for invalid
     * certificate by given exception that keeps the problem
     * occurred during the verification process.
     */
    public CertificateVerificationResult(Throwable exception) {
        this.valid = false;
        this.exception = exception;
    }

    public boolean isValid() {
        return valid;
    }

    public PKIXCertPathBuilderResult getResult() {
        return result;
    }

    public Throwable getException() {
        return exception;
    }
}