package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import com.ispirit.digitalsky.service.api.DigitalCertificateValidatorService;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.PEMReader;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitalCertificateValidatorServiceImpl implements DigitalCertificateValidatorService {

    public DigitalCertificateValidatorServiceImpl() {

    }

    @Override
    public boolean isValidCertificate(X509Certificate clientCertificate, String manufacturerCertificateChainPath) throws InvalidDigitalCertificateException {

        boolean found = false;
        try {
            InputStream inputstream = new FileInputStream(manufacturerCertificateChainPath);
            String certificateChainString = IOUtils.toString(inputstream, "UTF-8");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            final List<X509Certificate> certs = new ArrayList<>();
            final PEMReader reader = new PEMReader(new StringReader(certificateChainString));
            Certificate crt;

            while ((crt = (Certificate) reader.readObject()) != null) {
                if (crt instanceof X509Certificate) {
                    certs.add((X509Certificate) crt);
                }
            }
            if (certs.size() == 0) {
                throw new InvalidDigitalCertificateException();
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            CertPathValidator validator = CertPathValidator.getInstance("PKIX", "BC");
            Set<TrustAnchor> anchors = new HashSet<>();
            for (X509Certificate cert : certs) {
                anchors.add(new TrustAnchor(cert, null));
            }
            CertPath path = cf.generateCertPath(certs);
            PKIXParameters params = new PKIXParameters(anchors);
            params.setRevocationEnabled(false);

            validator.validate(path, params);
            for (X509Certificate trustedCertificate : certs) {
                if (!isDNMatching(clientCertificate.getIssuerDN().getName(), trustedCertificate.getSubjectDN().getName())) {
                    throw new InvalidDigitalCertificateException();
                }
                try {
                    if (isSelfSigned(trustedCertificate)) {
                        found = true;
                    } else if (!clientCertificate.equals(trustedCertificate)) {
                        clientCertificate = trustedCertificate;
                    }
                } catch (NoSuchProviderException e) {
                    throw new InvalidDigitalCertificateException();
                }
            }
        } catch(Exception e) {
            throw new InvalidDigitalCertificateException();
        }
        return found;
    }

    private boolean isDNMatching(String issuerDNName, String subjectDNName) {

        return isDNMatching(issuerDNName, subjectDNName, "cn")
                && isDNMatching(issuerDNName, subjectDNName, "o");
    }

    private boolean isDNMatching(String issuerDNName, String subjectDNName, String attribute) {
        boolean match = false;
        Pattern pattern = Pattern.compile(attribute + "=[\\w\\s\\.\\-]+",Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern.matcher(issuerDNName);
        Matcher matcher2 = pattern.matcher(subjectDNName);

        if(matcher1.find() && matcher2.find()) {
            String issuerAttribute = matcher1.group(0).substring(attribute.length());
            String subjectAttribute = matcher2.group(0).substring(attribute.length());
            match = issuerAttribute.equals(subjectAttribute);
        }

        return match;
    }

    private boolean isSelfSigned(X509Certificate cert)
            throws CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException {
        try {
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException e) {
            return false;
        }
    }
}

