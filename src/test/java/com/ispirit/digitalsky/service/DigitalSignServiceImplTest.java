package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.util.XmlUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DigitalSignServiceImplTest {

    private ResourceLoader resourceLoader;
    private DigitalSignServiceImpl digitalSignService;

    @Before
    public void setUp() throws Exception {
        resourceLoader = mock(ResourceLoader.class);
        InputStream certStream = this.getClass().getResourceAsStream("/cert.pem");
        InputStream privateKeyStream = this.getClass().getResourceAsStream("/key.pem");

        Resource privateKeyResource = mock(Resource.class);
        when(resourceLoader.getResource("privateKey")).thenReturn(privateKeyResource);
        when(privateKeyResource.getInputStream()).thenReturn(privateKeyStream);

        Resource certificateResource = mock(Resource.class);
        when(resourceLoader.getResource("publicCertificate")).thenReturn(certificateResource);
        when(certificateResource.getInputStream()).thenReturn(certStream);

        digitalSignService = new DigitalSignServiceImpl(resourceLoader,"privateKey","publicCertificate");
    }

    @Test
    public void shouldDigitallySignDocument() throws Exception {

        //when
        String signedDocument = digitalSignService.sign("<test><node>value</node></test>");

        //then
        Document document = XmlUtil.fromString(signedDocument);
        NodeList signatureNodes = document.getElementsByTagName("Signature");
        assertThat(signatureNodes.getLength(), is(1));

    }
}