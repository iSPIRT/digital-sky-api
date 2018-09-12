
package com.ispirit.digitalsky.controller.helper;

import com.ispirit.digitalsky.domain.DroneDevice;

import java.io.*;
import java.security.*;

public class DigitalSigner {

    private static final String KEY_STORE_TYPE = "JKS";

    private KeyStore.PrivateKeyEntry keyEntry;

    /**
     * Constructor
     * @param keyStoreFile - Location of .cer file
     * @param keyStorePassword - Password of .cer file
     * @param alias - Alias of the certificate in .cer file
     */
    public DigitalSigner(String keyStoreFile, char[] keyStorePassword, String alias) {
        this.keyEntry = getKeyFromKeyStore(keyStoreFile, keyStorePassword, alias);

        if (keyEntry == null) {
            throw new RuntimeException("Key could not be read for digital signature. Please check value of signature "
                    + "alias and signature password");
        }
    }

    //The method that signs the data using the private key that is stored in keyFile path
    public String sign(DroneDevice drone) throws InvalidKeyException, NoSuchAlgorithmException, IOException, SignatureException {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        SignedObject digitalSignedObj =
                new SignedObject(drone, keyEntry.getPrivateKey(), rsa);
        return digitalSignedObj.getSignature().toString();
    }

    private KeyStore.PrivateKeyEntry getKeyFromKeyStore(String keyStoreFile, char[] keyStorePassword, String alias) {
        // Load the KeyStore and get the signing key and certificate.
        FileInputStream keyFileStream = null;
        try {
            KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
            keyFileStream = new FileInputStream(keyStoreFile);
            ks.load(keyFileStream, keyStorePassword);

            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias,
                    new KeyStore.PasswordProtection(keyStorePassword));
            return entry;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (keyFileStream != null) {
                try {
                    keyFileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getCertificate() {
        try {
            return this.keyEntry.getCertificate().getEncoded().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
