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
        String val = "Wudc74tFowjSMNyVFwnHOw5aDYICx9tfTqknCbLae2XVyn6xKnir6uiOenA+kkIwicGxEQ+ewiVOlFmQSySO9jadxBW18LbmfyhGNaG+CSEo+" +
                "eWQEpSj+FLzaF2tD7tepL234QN8jWHhoxEtOy+a3NFohEeVS2p8lOonJIKnDKltLXyKCZ+4zn0XTr1o4ThZm8tYWFjBPhTkLhwM3wYtrzcL2sjIDche9/" +
                "0+9Cv1aqr8gXp6YIrrx6RNqRH3N0S4Zh4hlCpkmAIleBZ0E8fOl9QgU9HHEKk06RuqpnuqTXTBPSBaEvFOgOurWlDOMoCUxHPLjYX5lflb4n8jpDZx5g==";
        return val;
    }

    public static String getValidCertificateString() {
        String val = "MIIGRjCCBS6gAwIBAgISA4rxfbSwCQ365xQ3tooDnK1zMA0GCSqGSIb3DQEBCwUAMEoxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMw" +
                "IQYDVQQDExpMZXQncyBFbmNyeXB0IEF1dGhvcml0eSBYMzAeFw0xODA3MjcwMzMzMzZaFw0xODEwMjUwMzMzMzZaMDkxNzA1BgNVBAMTLmRpZ2l0YWxza3ktdWF0LmNlbnRy" +
                "YWxpbmRpYS5jbG91ZGFwcC5henVyZS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCf1m1Zph//wJeFxGUoLCKpGrLdx6dNCyywgEZv4XSDKKyxk3YJzebO4" +
                "YsKgQba/SrEwjBUBJ0YfDwoJFKR3hWI2jmGNcje0LlrAAewpCUEcjPZKpM6dVS6wyPu2UqzPkJyTEoLI8s5Zl44wmyptncf6nyIIPVlgwD7EUCv/Fc4JeL/+23Ey6Ks9NynRTbv" +
                "2MiR0BzcC1d9DhNx3BODaVBeavF7QglYcC7kJjnndAJj8FTYrCMRqUuOWO50VIzROVS61P3GmSaqZiRYu72WUk7mfjaXZ9waWRxhVNUbeJcjbQKjmaxw7gYcQS2gUQV0BAndhyMJ" +
                "YeLB3yAYGkZRXNgJAgMBAAGjggM1MIIDMTAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFMXnOx5Rg" +
                "+6FMx9I3Q5VQsP4xCQaMB8GA1UdIwQYMBaAFKhKamMEfd265tE5t6ZFZe/zqOyhMG8GCCsGAQUFBwEBBGMwYTAuBggrBgEFBQcwAYYiaHR0cDovL29jc3AuaW50LXgzLmxldHNlb" +
                "mNyeXB0Lm9yZzAvBggrBgEFBQcwAoYjaHR0cDovL2NlcnQuaW50LXgzLmxldHNlbmNyeXB0Lm9yZy8wOQYDVR0RBDIwMIIuZGlnaXRhbHNreS11YXQuY2VudHJhbGluZGlhLmNsb" +
                "3VkYXBwLmF6dXJlLmNvbTCB/gYDVR0gBIH2MIHzMAgGBmeBDAECATCB5gYLKwYBBAGC3xMBAQEwgdYwJgYIKwYBBQUHAgEWGmh0dHA6Ly9jcHMubGV0c2VuY3J5cHQub3JnMIGr" +
                "BggrBgEFBQcCAjCBngyBm1RoaXMgQ2VydGlmaWNhdGUgbWF5IG9ubHkgYmUgcmVsaWVkIHVwb24gYnkgUmVseWluZyBQYXJ0aWVzIGFuZCBvbmx5IGluIGFjY29yZGFuY2Ugd2l0" +
                "aCB0aGUgQ2VydGlmaWNhdGUgUG9saWN5IGZvdW5kIGF0IGh0dHBzOi8vbGV0c2VuY3J5cHQub3JnL3JlcG9zaXRvcnkvMIIBAwYKKwYBBAHWeQIEAgSB9ASB8QDvAHUAKTxRllT" +
                "IOWW6qlD8WAfUt2+/WHopctykwwz05UVH9HgAAAFk2gNygwAABAMARjBEAiA/uwyYmpPTIVXhQhVsKzkvDvhtixifymE8p5lmpq5lYQIgWr5AUdrqllhE4aL/8yLp60HsN9p0SCv" +
                "2lqG6wDkwl3QAdgBVgdTCFpA2AUrqC5tXPFPwwOQ4eHAlCBcvo6odBxPTDAAAAWTaA3OZAAAEAwBHMEUCIQDEmnbDex9GX5/WnPdWoNwqG9HHDHDtgGn8cgphk81yGAIgEjqnW1Le" +
                "DQzY6widD7vc1HzOLtm9Iyzf16eDJg4/VA4wDQYJKoZIhvcNAQELBQADggEBAA5egNTfNdlBfQm9ihG9maGAsKSqQOSFIH9uhgkDaop3sCVe2CA0GMCKUBtMyu3gHVLQwtdb2FOpUc" +
                "PDPoZ+8/avLEY7E6PR0dfva/YbNoZuoXvfvPvR7dnzsaHY2GpCIRUAxP9jK9BhbVdNrkmPhnzvoiV5fONj7dj5glzonSd0oQ17z4um+ryTiA8xng2riWtDB5+HQGL3MSxj3+a" +
                "QVMdrugG5eZBnOy8apIKrUCVFkDqmMERCkxRiYbGmrygtkHZNhUwon0up+grZ8VdjaEn6bjTKNEHVRRrXdcVE3oOC7ffDE4lr2a7o139ZNUaoCZtXKNVvBRhyIJkjN0EziSA=";

        return val;
    }


    public static DroneDevice getValidDroneDevice() {

        DroneDevice droneDevice = new DroneDevice("1.0","Beebop 900.0","1A29.0", "From manufacturer ", "some value", "eff217e740534fde89c1bfe62e08f316");
        return droneDevice;
    }
}
