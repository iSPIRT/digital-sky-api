package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.helper.DigitalSignerForTest;
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


import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContext.class)
public class RegisterDroneIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private RestTemplate patchRestTemplate;

    private final String keyStoreFile = "digitalsky.jks";
    private final String keyStorePassword = "password";
    private final String alias = "digitalsky";
    private String keyStorePath;
    private DigitalSignerForTest digitalSigner;

    @Before
    public void setup() {
        this.patchRestTemplate = restTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        File resourcesDirectory = new File("src/test/resources");
        keyStorePath = resourcesDirectory.getAbsolutePath() + "/" + keyStoreFile;
        digitalSigner = new DigitalSignerForTest(keyStorePath, keyStorePassword.toCharArray(), alias);
    }

    @Test
    public void registerDrone()  {

        try {
            DroneDevice mockDrone = new DroneDevice("1.0","Beebop 900.0","1A29.0", "From manufacturer ", "some value", "eff217e740534fde89c1bfe62e08f316");
            String signature = digitalSigner.sign(mockDrone);
            String certificate = digitalSigner.getBase64EncodedCertificate();
            RegisterDroneRequestPayload mockDronePayload = new RegisterDroneRequestPayload(mockDrone, signature, certificate );

            ResponseEntity<RegisterDroneResponsePayload> responseEntity =
                    restTemplate.postForEntity("/api/droneDevice/register/8ccf320028554028b47dbc3441d058c0",  mockDronePayload, RegisterDroneResponsePayload.class);

            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            assertEquals(responseEntity.getBody().getTxn(), mockDrone.getTxn());
            assertEquals(responseEntity.getBody().getResponseCode(), RegisterDroneResponseCode.REGISTERED);
            assertEquals(responseEntity.getBody().getError(), null);
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
    }

    @Test
    public void deregisterDrone()  {

        try {
            DroneDevice mockDrone = new DroneDevice("1.0","Beebop 300.0","1A29.0", "From manufacturer ", "some value");
            String signature = digitalSigner.sign(mockDrone);
            String certificate = digitalSigner.getBase64EncodedCertificate();
            RegisterDroneRequestPayload mockDronePayload = new RegisterDroneRequestPayload(mockDrone, signature, certificate );

            RegisterDroneResponsePayload responsePayload =
                    patchRestTemplate.patchForObject("/api/droneDevice/deregister/8ccf320028554028b47dbc3441d058c0", mockDronePayload, RegisterDroneResponsePayload.class);

            assertEquals(responsePayload.getTxn(), mockDrone.getTxn());
            assertEquals(responsePayload.getResponseCode(), RegisterDroneResponseCode.DEREGISTERED);
            assertEquals(responsePayload.getError(), null);
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
    }
}