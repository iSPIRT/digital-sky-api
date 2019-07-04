package com.ispirit.digitalsky.helper;

import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class DigitalSignatureVerifierForTest {

    public static X509Certificate generateX509CertificateFromBase64EncodedString(String certString) throws InvalidDigitalCertificateException {

        InputStream inputStream = null;
        try {
            X509Certificate certificate = (X509Certificate) CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(
                            new ByteArrayInputStream(Base64Utils.decode(certString.getBytes()))
                    );
            return certificate;
        } catch (CertificateException e) {
            throw new InvalidDigitalCertificateException();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new InvalidDigitalCertificateException();
                }
            }
        }
    }

    public static String getValidSignatureString() {
        String val = "Z23BwgxQfBscgMm4a6/F6jTEO+yz4F6s0CPO8yUMhVez/Y4nPdIcNOfwk8qQbII73fL+Uthb74GGENqV/ziHqnwzKF995RYuocOIR6ZSPr0QGLY8bKFwXsApeNOBVy/tAYtD/sESOh8mpxxfnIr02Yu9tHwXJ4/+27UUwPtX3pxSQy06ccFV42/kJPxVcIFdjX0YZDVudef6sWgU/iDd7Dz0w7lI6Qky4yBChEgwY7G+1eQKP4+mdL3c98q3l9a42XMFZSKvotZtaZvdnwdMey1GRoGIIoConEOGTaO6p8lVP4N5j7rPLjVsz/2nl6LHn1OwpnNvdDnbFNDoLwrBWA==";
        return val;
    }

    public static String getValidCertificateString() {
        String val = "MIIDzjCCAragAwIBAgIEI85K6TANBgkqhkiG9w0BAQsFADCBlzELMAkGA1UEBhMCSU4xEjAQBgNVBAgMCUthcm5hdGFrYTESMBAGA1UEBwwJQmFuZ2Fsb3JlMR8wHQYDVQQKDBZTYWhhanNvZnR3YXJlc29sdXRpb25zMREwDwYDVQQLDAhzZXJ2aWNlczEsMCoGA1UEAwwjc2FoYWpzb2Z0d2FyZXNvbHV0aW9ucy9lbWFpbEFkZHJlc3MwHhcNMTkwMTIzMDcxMTQ4WhcNMjUxMjMwMTgzMDAwWjCBlzELMAkGA1UEBhMCSU4xEjAQBgNVBAgMCUthcm5hdGFrYTESMBAGA1UEBwwJQmFuZ2Fsb3JlMR8wHQYDVQQKDBZTYWhhanNvZnR3YXJlc29sdXRpb25zMREwDwYDVQQLDAhzZXJ2aWNlczEsMCoGA1UEAwwjc2FoYWpzb2Z0d2FyZXNvbHV0aW9ucy9lbWFpbEFkZHJlc3MwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCeeHRT5g2G1uQAC9NgPQY8Uw/AYnsLX1KT8IWpvdmOa2B75Qh7sD4qNLGSDBiXLIkQv7rS0z2Lpo+gA7CGGuBRFzBPTKju0spdGOjHEB9CIpW2/7fKLNyQ59Icd3ltjMAiQOIChibw53JtytSS5jieueGHve8xKC/SMmUVu8+j2nwDzrV42jj1Wsu7wK3DZSYcMv+ORj7k31z1ui1EhhYOaSi3LUE+A8pF4+fDSOUBnbmD7t9Wux84/ujMdth9wwsmSzS9hDyv3l/aLMf4jysgJgErqJcQc8i0aN4XTJ2uu0X+yxXZ5fr00s5nXWd2iy2FT8pFwjhljUgOwt9xZM5ZAgMBAAGjIDAeMA4GA1UdDwEB/wQEAwICBDAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQAWnbhK8RL0K/wPRW6sgq7mDI5naBVVyHLWR1zkxdxJdfcYLs0fJsKJjyvmKU2tX4CnVwlf5LHP4/YVdID7y70m2VwN/HSZ1vMW6RjdqTQ3k5AtXDG3XXOwlZmIOmXoGwyLg1L1aoaP7WTp0r7qeBNISOP1AYcgPPju3m2V5DH9z6sfiX9q8hTf5U8eah6sQQRKMrg1ewZ48flcFiAJgpIHpyU+UhBdA1IaXDG8JmKa+QfuyUarviMuIa3OFpeWl9vHAHoc2R44te3EiZkWhEqUPEhLiba6s38sLUfLjm0ipQBTtHvfcrSJnUciscaLA5mCaQfLOmFFyA4JV6vJrBak";

        return val;
    }


    public static DroneDevice getValidDroneDevice() {

        DroneDevice droneDevice = new DroneDevice("55556","10","1.0", "test", null, "5fdbbd3e439a4457b5ae59068120f613",1);
        return droneDevice;
    }
}
