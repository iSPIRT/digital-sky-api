package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.SpecialContext;
import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.helper.DigitalSignerForTest;

import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.service.api.DroneDeviceService;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.util.CustomValidator;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;


import static com.ispirit.digitalsky.SecurityContextHelper.setUserSecurityContext;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SpecialContext.class)
@Ignore
public class RegisterDroneIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private RestTemplate patchRestTemplate;

    private final String keyStoreFile = "digitalsky.jks";
    private final String keyStorePassword = "password";
    private final String alias = "digitalsky";
    private String keyStorePath;
    private DigitalSignerForTest digitalSigner;

    @MockBean
    ManufacturerService manufacturerService;

    private UserPrincipal userPrincipal;

    @Autowired
    DroneDeviceService droneDeviceService;

    @Autowired
    private CustomValidator customValidator;

    @Before
    public void setup() {
        this.patchRestTemplate = restTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        File resourcesDirectory = new File("src/test/resources");
        keyStorePath = resourcesDirectory.getAbsolutePath() + "/" + keyStoreFile;
        digitalSigner = new DigitalSignerForTest(keyStorePath, keyStorePassword.toCharArray(), alias);
        userPrincipal = setUserSecurityContext();
    }


    @Test
    public void registerDrone()  {

        try {
            DroneDevice mockDrone = new DroneDevice("BeebopB800","1.0","1A29.0", "From manufacturer ", "some value", "eff217e740534fde89c1bfe62e08f316",1);
            String signature = digitalSigner.sign(mockDrone);
            String certificate = digitalSigner.getBase64EncodedCertificate();
            RegisterDroneRequestPayload mockDronePayload = new RegisterDroneRequestPayload(mockDrone, signature, certificate );

            Address address = new Address("LineOne", "LineTwo", "City", "State", "Country", "560001");

            Manufacturer manufacturer = new Manufacturer(userPrincipal.getId(), null, "digitalsky-uat.centralindia.cloudapp.azure.com", "sample@email.com", "1234567", "9999999", "India",  asList(address));
            manufacturer.setId(1);

            InputStream resource = this.getClass().getResourceAsStream("/Users/charana/Documents/digital-sky-api/src/test/resources/trustedCertificateChain.pem");
            MockMultipartFile trustedCertificateDoc = new MockMultipartFile("trustedCertificateDoc", "trustedCertificateChain.pem", MediaType.MULTIPART_FORM_DATA_VALUE, resource);

            manufacturer.setTrustedCertificateDoc(trustedCertificateDoc);

            when(manufacturerService.loadByBusinessIdentifier(any(String.class))).thenReturn(manufacturer);

            when(manufacturerService.getCAAndTrustedCertificatePath(any(long.class))).thenReturn("/Users/charana/Documents/digital-sky-api/src/test/resources/trustedCertificateChain.pem");

            customValidator.validate(mockDronePayload.getDrone());
            DroneDevice savedDevice = droneDeviceService.register("123", mockDronePayload);

            RegisterDroneResponsePayload responsePayload = new RegisterDroneResponsePayload();
            ResponseEntity<?> responseEntity = getResponseEntityForRegistration(responsePayload, RegisterDroneResponseCode.REGISTERED, savedDevice.getTxn());

            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            assertEquals(responsePayload.getTxn(), mockDrone.getTxn());
            assertEquals(responsePayload.getResponseCode(), RegisterDroneResponseCode.REGISTERED);
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

    private ResponseEntity<?> getResponseEntityForRegistration(RegisterDroneResponsePayload payload, RegisterDroneResponseCode responseCode, String txn ) {
        payload.setResponseCode(responseCode);
        payload.setTxn(txn);
        return new ResponseEntity<>(payload, HttpStatus.CREATED);
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