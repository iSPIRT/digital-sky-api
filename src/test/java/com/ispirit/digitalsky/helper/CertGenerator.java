package com.ispirit.digitalsky.helper;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

public class CertGenerator {

  public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException, InvalidKeyException, NoSuchProviderException, SignatureException, UnrecoverableKeyException, UnrecoverableEntryException {
    Security.addProvider(new BouncyCastleProvider());
    char[] password = "password".toCharArray(); //type your password here

    //C=IN, ST=Karnataka, L=Bangalore, O=Sahajsoftwaresolutions, OU=services, CN=sahajsoft.com/emailAddress=charana@sahajsoft.com
    // Create self signed Root CA certificate
    KeyPair rootCAKeyPair = generateKeyPair();
    X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
        new X500Name("C=IN, ST=Karnataka, L=Bangalore, O=Sahajsoftwaresolutions, OU=services, CN=sahajsoftwaresolutions/emailAddress=charana@sahajsoft.com"), // issuer authority
        BigInteger.valueOf(new Random().nextInt()), //serial number of certificate
        Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), // start of validity
        Date.from(LocalDateTime.of(2025,12,31,0,0,0).atZone(ZoneId.systemDefault()).toInstant()), //end of certificate validity
        new X500Name("C=IN, ST=Karnataka, L=Bangalore, O=Sahajsoftwaresolutions, OU=services, CN=sahajsoftwaresolutions/emailAddress=charana@sahajsoft.com"), // subject name of certificate
        rootCAKeyPair.getPublic()); // public key of certificate
    // key usage restrictions
    builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign));
    builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
    X509Certificate rootCA = new JcaX509CertificateConverter().getCertificate(builder
        .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").
            build(rootCAKeyPair.getPrivate()))); // private key of signing authority , here it is self signed

    storeToPKCS12("rootCA.pkcs12",password,rootCAKeyPair,rootCA);


    //create Intermediate CA cert signed by Root CA
    KeyPair intermedCAKeyPair = generateKeyPair();
    builder = new JcaX509v3CertificateBuilder(
        rootCA, // here rootCA is issuer authority
        BigInteger.valueOf(new Random().nextInt()), Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
        Date.from(LocalDateTime.of(2025,12,31,0,0,0).atZone(ZoneId.systemDefault()).toInstant()),
        new X500Name("C=IN, ST=Karnataka, L=Bangalore, O=Sahajsoftwaresolutions, OU=services, CN=sahajsoftwaresolutions/emailAddress=charana@sahajsoft.com"), intermedCAKeyPair.getPublic());
    builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign));
    builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
    X509Certificate intermedCA = new JcaX509CertificateConverter().getCertificate(builder
        .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").
            build(rootCAKeyPair.getPrivate())));// private key of signing authority , here it is signed by rootCA

    storeToPKCS12("interCA.pkcs12",password,intermedCAKeyPair,intermedCA);

    //create end user cert signed by Intermediate CA
    KeyPair endUserCertKeyPair = generateKeyPair();
    builder = new JcaX509v3CertificateBuilder(
        intermedCA, //here intermedCA is issuer authority
        BigInteger.valueOf(new Random().nextInt()), Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
        Date.from(LocalDateTime.of(2025,12,31,0,0,0).atZone(ZoneId.systemDefault()).toInstant()),
        new X500Name("C=IN, ST=Karnataka, L=Bangalore, O=Sahajsoftwaresolutions, OU=services, CN=sahajsoftwaresolutions/emailAddress=charana@sahajsoft.com"), endUserCertKeyPair.getPublic());
    builder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
    builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
    X509Certificate endUserCert = new JcaX509CertificateConverter().getCertificate(builder
        .build(new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").
            build(intermedCAKeyPair.getPrivate())));// private key of signing authority , here it is signed by intermedCA

    storeToPKCS12("endUserCA.pkcs12",password,endUserCertKeyPair,endUserCert);


    String messageString = "{\"version\":\"1.0\",\"txn\":\"test\",\"deviceId\":\"55556\",\"deviceModelId\":\"10\",\"operatorBusinessIdentifier\":\"5fdbbd3e439a4457b5ae59068120f613\",\"idHash\":null}";
    KeyPair retrievedKeyPair = loadFromPKCS12("interCA.pkcs12", password);
    RSAPublicKey pubKey = (RSAPublicKey) retrievedKeyPair.getPublic();
    RSAPrivateKey privKey = (RSAPrivateKey) retrievedKeyPair.getPrivate();
    System.out.println(pubKey.getModulus().equals(privKey.getModulus()));
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    Signature signature = Signature.getInstance("SHA1withRSA", "BC");
    signature.initSign(privKey, new SecureRandom());
    byte[] message = messageString.getBytes("UTF8");
    signature.update(message);
    byte[] sigBytes = signature.sign();
    System.out.println("\nSignature:"+ new String(Base64.getEncoder().encode(sigBytes)));

  }

  private static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
    KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
    kpGen.initialize(2048, new SecureRandom());
    return kpGen.generateKeyPair();
  }

  private static void storeToPKCS12(
      String filename, char[] password,
      KeyPair generatedKeyPair, X509Certificate selfSignedCertificate) throws KeyStoreException, IOException,
      NoSuchAlgorithmException, CertificateException,
      OperatorCreationException {


    KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");
    pkcs12KeyStore.load(null, null);

    KeyStore.Entry entry = new KeyStore.PrivateKeyEntry(generatedKeyPair.getPrivate(),
        new X509Certificate[] { selfSignedCertificate });
    KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(password);

    pkcs12KeyStore.setEntry("sahajsoftwaresolutions", entry, param);

    try (FileOutputStream fos = new FileOutputStream(filename)) {
      pkcs12KeyStore.store(fos, password);
    }
  }

  private static KeyPair loadFromPKCS12(String filename, char[] password)
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
      FileNotFoundException, IOException, UnrecoverableEntryException {
    KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");

    try (FileInputStream fis = new FileInputStream(filename);) {
      pkcs12KeyStore.load(fis, password);
    }

    KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(password);
    KeyStore.Entry entry = pkcs12KeyStore.getEntry("sahajsoftwaresolutions",param);
    if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
      throw new KeyStoreException("That's not a private key!");
    }
    java.security.cert.Certificate cert = ((KeyStore.PrivateKeyEntry) entry).getCertificate();
    byte[] rawOctets = cert.getEncoded();
    SHA1Digest digest = new SHA1Digest();
    byte[] digestOctets = new byte[digest.getDigestSize()];
    digest.update(rawOctets, 0, rawOctets.length);
    digest.doFinal(digestOctets, 0);
    System.out.println("CERT:");
    System.out.println(new String(Base64.getEncoder().encode(cert.getEncoded())));
    KeyStore.PrivateKeyEntry privKeyEntry = (KeyStore.PrivateKeyEntry) entry;
    PublicKey publicKey = privKeyEntry.getCertificate().getPublicKey();
    PrivateKey privateKey = privKeyEntry.getPrivateKey();
    return new KeyPair(publicKey, privateKey);
  }
}
