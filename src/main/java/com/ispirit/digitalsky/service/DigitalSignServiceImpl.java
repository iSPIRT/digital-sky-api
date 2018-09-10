package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.service.api.DigitalSignService;
import com.ispirit.digitalsky.util.XmlUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static java.util.Collections.singletonList;

public class DigitalSignServiceImpl implements DigitalSignService {


    private final X509Certificate certificate;
    private final PrivateKey privateKey;

    public DigitalSignServiceImpl(ResourceLoader resourceLoader, String digitalSkyPrivateKeyPath, String digitalSkyCertificate) {
        try {
            certificate = loadCertificate(resourceLoader.getResource(digitalSkyCertificate).getInputStream());
            privateKey = loadPrivateKey(resourceLoader.getResource(digitalSkyPrivateKeyPath).getInputStream());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private X509Certificate loadCertificate(InputStream certificateStream) throws CertificateException {

        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        return (X509Certificate) fact.generateCertificate(certificateStream);
    }

    private PrivateKey loadPrivateKey(InputStream privateKey) throws Exception {

        String privateKeyString = IOUtils.toString(privateKey, "UTF-8");

        privateKeyString = privateKeyString.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));

        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(spec);
    }


    @Override
    public String sign(String xmlDocument) {
        try {
            Document document = XmlUtil.fromString(xmlDocument);
            XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
            DOMSignContext domSignCtx = new DOMSignContext(privateKey, document.getDocumentElement());

            Reference ref = xmlSigFactory.newReference(
                    "",
                    xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null),
                    singletonList(xmlSigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null,
                    null
            );

            SignedInfo signedInfo = xmlSigFactory.newSignedInfo(
                    xmlSigFactory.newCanonicalizationMethod(
                            CanonicalizationMethod.INCLUSIVE,
                            (C14NMethodParameterSpec) null),
                    xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    singletonList(ref)
            );

            XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, getKeyInfo());

            xmlSignature.sign(domSignCtx);

            return XmlUtil.documentToString(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private KeyInfo getKeyInfo() {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        KeyInfoFactory keyInfoFactory = fac.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(certificate.getSubjectX500Principal().getName());
        x509Content.add(certificate);
        X509Data xd = keyInfoFactory.newX509Data(x509Content);
        return keyInfoFactory.newKeyInfo(singletonList(xd));
    }
}
