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
        String val = "W/VikaaEHyh4mSRJJ4i+V0gYXMbWHt1EoVH79o7Wgd4xotfZ9CHOMMtS/O7tA+8Fd2T0h12qk8oKrXibuelE9/LKMxYWXVrjJJfJG5QdrLm874C8SE1d9rc+Bo/EahEZaYT+5VaQFDfgrGekJAtNZZnejzSCSe2u+SqJ+rJt0L0r6Zwz9Uuu7OrWOQJ/+EKFTXJ9gTu8C0F80+zzvArbUH5D2MCjqYhjOc/eqHpEM4vbSOvrRXNzIhjCHl9P9lHVPmNSD/kwoLdJM2IvY82TTCJVbZOFF/xSbyvdkhQYiUoYluskcySEO4UVXTPUSeT/klQ5dL2nnJUHWARE1LTncw==";
        return val;
    }

    public static String getValidCertificateString() {
        String val = "MIIDmDCCAoCgAwIBAgIEak9nJjANBgkqhkiG9w0BAQsFADB9MQswCQYDVQQGEwJJTjESMBAGA1UECAwJS2FybmF0YWthMRIwEAYDVQQHDAlCYW5nYWxvcmUxEjAQBgNVBAoMCWd1ZXJyaWxsYTERMA8GA1UECwwIc2VydmljZXMxHzAdBgNVBAMMFmd1ZXJyaWxsYS9lbWFpbEFkZHJlc3MwHhcNMTkwNzE1MDkzMzQ1WhcNMjUxMjMwMTgzMDAwWjB9MQswCQYDVQQGEwJJTjESMBAGA1UECAwJS2FybmF0YWthMRIwEAYDVQQHDAlCYW5nYWxvcmUxEjAQBgNVBAoMCWd1ZXJyaWxsYTERMA8GA1UECwwIc2VydmljZXMxHzAdBgNVBAMMFmd1ZXJyaWxsYS9lbWFpbEFkZHJlc3MwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCNesWsIRAKUcssuQnsxlAw7z8ZrQPFaizDjMCOvLBTnPbW+kkaAcwm74EhbmOeyEB9w0mvwhqmJs3NGnOvQ6EFqSA0Mf9OpDZGxZBK2QR3GXjkc5jLJBeLHwEtAG2lyTokRUpsdnOMkDekOsqUafQmR04sWmdWwLrPCTx06tW8v5YQNWcLarSJK7Hh4DMzj3D/9BhWAXfap4n/cYHGak4v32oxV/D1xgVRgUyhH/v1i+FaqYh15jyj4SViud3V/hG9BFDu/nMGSZuIfopiZ8Kw5jj7V2/b5APOStMSoXd51ZQg/UqHxKdYpPsAcD4f+e1W6I0tElWivnML8vyVnBYRAgMBAAGjIDAeMA4GA1UdDwEB/wQEAwICBDAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQAjpB7CtqyGNDEK9YI9oT7jExTvE7Z5Gnhc/HQVLlAEpvs5fHpwgNYjnEuzK4r7zXydSj7nOMy7NsHLEYqftKXOuJkQcNvuVQkzyeq8qHxS5dGvjO++Ql9p6wHGLv3ea3tuRUsowKHqlC5wWUYluYLFYPEtsT3/k5uWPlfbvb50N8wlu8txRq5b/VVH61jQS1DO9ZHgXpl9XVlddsOykdzwYF4+W1opRnZQT1vpzaxLdrGcfLLomgAGdxFluYL/JKMsKvy0eZe96P2LJ2qWXF06Z0czk91/p9gXNfwJplNUrEaboZ0CrXi5S5WhWQO2iJAtZlSdSmbbZXJ5KK9phZHD";

        return val;
    }


    public static DroneDevice getValidDroneDevice() {

        DroneDevice droneDevice = new DroneDevice("56456","10","1.0", "test", null, "c8af793ab2ed4c80913e820f54ef6cff",9);
        return droneDevice;
    }
}
