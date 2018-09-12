package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContext.class)
public class RegisterDroneIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private RestTemplate patchRestTemplate;

    private DigitalSigner digitalSigner;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        digitalSigner = new DigitalSigner("/Users/archana/keystore.jks","changeit".toCharArray(), "digitalsky");

        // STEP #2
        // Add Apache HttpClient as TestRestTemplate
        this.patchRestTemplate = restTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Test
    public void registerDrone()  {

        RegisterDroneRequestPayload mockDronePayload = new RegisterDroneRequestPayload();
        DroneDevice mockDrone = new DroneDevice();

        mockDrone.setVersion("1.0");
        mockDrone.setDeviceId("RPA Beebop 9.0");
        mockDrone.setDeviceModelId("1A29.0");
        mockDrone.setTxn("BeebopValidValue");
        mockDrone.setOperatorCode("1");
        mockDrone.setIdHash("some value");
        mockDrone.setRequestTimestamp(LocalDateTime.now());

        mockDronePayload.setDrone(mockDrone);
        mockDronePayload.setDigitalCertificate(digitalSigner.getCertificate());
        try {
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
                restTemplate.postForEntity("/api/droneDevice/register/1",  mockDronePayload, RegisterDroneResponsePayload.class);
        String txn = responseEntity.getBody().getTxn();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(txn, "BeebopValidValue");
        assertEquals(responseEntity.getBody().getCode(), RegisterDroneResponseCode.REGISTERED);
        assertEquals(responseEntity.getBody().getError(), null);
    }


    public void deregisterDrone()  {

        RegisterDroneRequestPayload mockDronePayload = new RegisterDroneRequestPayload();
        DroneDevice mockDrone = new DroneDevice();

        mockDrone.setVersion("1.0");
        mockDrone.setDeviceId("RPA Beebop 4.0");
        mockDrone.setDeviceModelId("1A29.0");
        mockDrone.setTxn("BeebopValidValue");
        mockDrone.setOperatorCode("1");
        mockDrone.setIdHash("some value");
        mockDrone.setRequestTimestamp(LocalDateTime.now());

        mockDronePayload.setDrone(mockDrone);
        mockDronePayload.setDigitalCertificate(digitalSigner.getCertificate());
        try {
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


        // STEP #3
        // Use patchRestTemplate to make call with PATCH method
        RegisterDroneResponsePayload responsePayload =
                patchRestTemplate.patchForObject("/api/droneDevice/deregister/1", mockDronePayload, RegisterDroneResponsePayload.class);
//        RegisterDroneResponsePayload responsePayload =
//                restTemplate.patchForObject("",  mockDronePayload, RegisterDroneResponsePayload.class);
        String txn = responsePayload.getTxn();

        //assertEquals(HttpStatus.CREATED, responsePayload.);
        assertEquals(txn, "BeebopValidValue");
        assertEquals(responsePayload.getCode(), RegisterDroneResponseCode.DEREGISTERED);
        assertEquals(responsePayload.getError(), null);
    }
}