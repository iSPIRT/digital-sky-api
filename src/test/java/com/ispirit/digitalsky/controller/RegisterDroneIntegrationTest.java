package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.controller.helper.DigitalSigner;
import com.ispirit.digitalsky.domain.DroneDevice;
import com.ispirit.digitalsky.domain.RegisterDroneRequestPayload;
import com.ispirit.digitalsky.domain.RegisterDroneResponseCode;
import com.ispirit.digitalsky.domain.RegisterDroneResponsePayload;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;


import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContext.class)
public class RegisterDroneIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private RestTemplate patchRestTemplate;
    private final String keyStoreFile = "digitalsky.jks";
    private String keyStorePath;
    private final String keyStorePassword = "password";
    private final String alias = "digitalsky";

    private DigitalSigner digitalSigner;

    @Before
    public void setup() throws IOException {
        this.patchRestTemplate = restTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        File resourcesDirectory = new File("src/test/resources");
        keyStorePath = resourcesDirectory.getAbsolutePath() + "/" + keyStoreFile;
        digitalSigner = new DigitalSigner(keyStorePath, keyStorePassword.toCharArray(), alias);
    }

    @Test
    public void registerDrone()  {

        RegisterDroneRequestPayload mockDronePayload = new RegisterDroneRequestPayload();
        DroneDevice mockDrone = new DroneDevice();

        mockDrone.setVersion("1.0");
        mockDrone.setDeviceId("Beebop 600.0");
        mockDrone.setDeviceModelId("1A29.0");
        mockDrone.setTxn("From manufacturer ");
        //mockDrone.setOperatorCode("178968bec6414af99d79a69518a8306e");
        mockDrone.setOperatorCode("eff217e740534fde89c1bfe62e08f316");
        mockDrone.setIdHash("some value");

        mockDronePayload.setDrone(mockDrone);
        String certificate;

        try {
            certificate = digitalSigner.getBase64EncodedCertificate();
            mockDronePayload.setDigitalCertificate(certificate);
            mockDronePayload.setSignature(digitalSigner.sign(mockDrone));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        ResponseEntity<RegisterDroneResponsePayload> responseEntity =
                restTemplate.postForEntity("/api/droneDevice/register/8ccf320028554028b47dbc3441d058c0",  mockDronePayload, RegisterDroneResponsePayload.class);

        String txn = responseEntity.getBody().getTxn();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(txn, "From manufacturer ");
        assertEquals(responseEntity.getBody().getCode(), RegisterDroneResponseCode.REGISTERED);
        assertEquals(responseEntity.getBody().getError(), null);
    }


    @Test
    public void deregisterDrone()  {

        RegisterDroneRequestPayload mockDronePayload = new RegisterDroneRequestPayload();
        DroneDevice mockDrone = new DroneDevice();

        mockDrone.setVersion("1.0");
        mockDrone.setDeviceId("Beebop 300.0");
        mockDrone.setDeviceModelId("1A29.0");
        mockDrone.setTxn("From manufacturer ");
        mockDrone.setIdHash("some value");

        mockDronePayload.setDrone(mockDrone);
        String certificate;
        try {
            certificate = digitalSigner.getBase64EncodedCertificate();
            mockDronePayload.setDigitalCertificate(certificate);
            mockDronePayload.setSignature(digitalSigner.sign(mockDrone));
            mockDronePayload.setSignature(digitalSigner.sign(mockDrone));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        RegisterDroneResponsePayload responsePayload =
                patchRestTemplate.patchForObject("/api/droneDevice/deregister/8ccf320028554028b47dbc3441d058c0", mockDronePayload, RegisterDroneResponsePayload.class);
        String txn = responsePayload.getTxn();

        assertEquals(txn, "From manufacturer ");
        assertEquals(responsePayload.getCode(), RegisterDroneResponseCode.DEREGISTERED);
        assertEquals(responsePayload.getError(), null);
    }
}