package com.ispirit.digitalsky.exception;

public class ManufacturerTrustedCertificateNotFoundException extends RuntimeException {
    public ManufacturerTrustedCertificateNotFoundException() {
        super("Manufacturer Trusted Digital Certificate Not Uploaded");
    }
}
