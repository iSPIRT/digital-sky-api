package com.ispirit.digitalsky.controller.helper;

import org.springframework.util.Base64Utils;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

public class CertificateUtil {
    private static final String keyStorePath = "/Users/archana/certchain.jks";
    private static final String keyStorePassword = "password";
    private static final String keyStoreAlias = "digitalsky";

//    public static String getBase64EncodedCertificateChain() {
//        try {
//            KeyStore keyStore = KeyStore.getInstance("JKS");
//            keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
//            Enumeration<String> es = keyStore.aliases();
//            Certificate[] chain = keyStore.getCertificateChain(keyStoreAlias);
//            ArrayList<Certificate> certificates = new ArrayList<Certificate>();
//            for (int i = 0; i < chain.length; i++) {
//                certificates.add(chain[i]);
//            }
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
//            for(Certificate certificate : certificates){
//                    outputStream.write(certificate.getEncoded());
//                }
//
//            return Base64Utils.encodeToString(outputStream.toByteArray());
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//     return  null;
//    }

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
