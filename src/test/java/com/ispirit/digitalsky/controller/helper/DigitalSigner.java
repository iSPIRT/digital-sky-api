
package com.ispirit.digitalsky.controller.helper;

import com.ispirit.digitalsky.domain.DroneDevice;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.security.*;

public class DigitalSigner {

    private static final String KEY_STORE_TYPE = "JKS";

    private KeyStore.PrivateKeyEntry keyEntry;

    public DigitalSigner(String keyStoreFile, char[] keyStorePassword, String alias) {
        this.keyEntry = getKeyFromKeyStore(keyStoreFile, keyStorePassword, alias);

        if (keyEntry == null) {
            throw new RuntimeException("Key could not be read for digital signature. Please check value of signature "
                    + "alias and signature password");
        }
    }

    public String sign(DroneDevice drone) throws InvalidKeyException, NoSuchAlgorithmException, IOException, SignatureException {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(keyEntry.getPrivateKey());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(drone);
            oos.flush();
            rsa.update(baos.toByteArray());
            byte[] signedDroneDeviceObj = rsa.sign();
            return Base64Utils.encodeToString(signedDroneDeviceObj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KeyStore.PrivateKeyEntry getKeyFromKeyStore(String keyStoreFile, char[] keyStorePassword, String alias) {
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
}
