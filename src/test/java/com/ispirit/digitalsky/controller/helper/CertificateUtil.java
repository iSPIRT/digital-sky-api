package com.ispirit.digitalsky.controller.helper;

import org.springframework.util.Base64Utils;

import java.io.*;
import java.security.*;
import java.security.cert.*;

public class CertificateUtil {
    private static final String keyStorePath = "/Users/archana/certchain.jks";
    private static final String keyStorePassword = "password";
    private static final String keyStoreAlias = "digitalsky";

    public static String getBase64EncodedCertificate() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyStoreAlias);
            return Base64Utils.encodeToString(certificate.getEncoded());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

}
