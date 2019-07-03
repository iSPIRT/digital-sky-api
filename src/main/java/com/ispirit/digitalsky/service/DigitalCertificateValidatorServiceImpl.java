package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.exception.InvalidDigitalCertificateException;
import com.ispirit.digitalsky.service.api.DigitalCertificateValidatorService;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMParser;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitalCertificateValidatorServiceImpl implements DigitalCertificateValidatorService {

    private boolean selfSignedValidity;
    private String ccaCertificatePath;

    public DigitalCertificateValidatorServiceImpl(boolean selfSignedValidity, String ccaCertificatePath) {
        this.selfSignedValidity=selfSignedValidity;
        this.ccaCertificatePath = ccaCertificatePath;
    }

    @Override
    public boolean isValidCertificate(X509Certificate clientCertificate, String manufacturerCertificateChainPath) throws InvalidDigitalCertificateException {

        boolean found = false;
        try {
            InputStream inputstream = new FileInputStream(manufacturerCertificateChainPath);
            String certificateChainString = IOUtils.toString(inputstream, "UTF-8");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            final List<X509Certificate> certs = new ArrayList<>();
            final PEMParser reader = new PEMParser(new StringReader(certificateChainString));
            X509CertificateHolder crt;
            Certificate cert;

            while ((crt = (X509CertificateHolder) reader.readObject()) != null) {
                cert = new JcaX509CertificateConverter().setProvider( "BC" )
                    .getCertificate( crt );
                if (cert instanceof X509Certificate) {
                    certs.add((X509Certificate) cert);
                }
            }
            if (certs.size() == 0) {
                throw new InvalidDigitalCertificateException();
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            CertPathValidator validator = CertPathValidator.getInstance("PKIX", "BC");
            Set<TrustAnchor> anchors = new HashSet<>();
            if (!selfSignedValidity){
                inputstream = new FileInputStream(ccaCertificatePath);
                certificateChainString = IOUtils.toString(inputstream, "UTF-8");
                PEMParser rootCaReader = new PEMParser(new StringReader(certificateChainString));
                X509CertificateHolder rootCertHolder = (X509CertificateHolder) rootCaReader.readObject();
                anchors.add(new TrustAnchor(new JcaX509CertificateConverter().setProvider( "BC" ).getCertificate( rootCertHolder ),null));
            }
            for (X509Certificate certif : certs) {
                anchors.add(new TrustAnchor(certif, null));
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
                    if (isSelfSigned(trustedCertificate) && selfSignedValidity) {
                        found = true;
                    } else if (!clientCertificate.equals(trustedCertificate)) {
                        clientCertificate = trustedCertificate; //todo: figure out why this line exists
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

    private boolean isSelfSigned(X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException e) {
            return false;
        }
    }
}

