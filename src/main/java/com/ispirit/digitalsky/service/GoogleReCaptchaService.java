package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.exception.ReCaptchaVerificationFailedException;
import com.ispirit.digitalsky.service.api.ReCaptchaService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class GoogleReCaptchaService implements ReCaptchaService {


    private RestTemplate restTemplate;
    private String reCaptchaVerifyUrl;
    private String siteSecret;

    public GoogleReCaptchaService(RestTemplate restTemplate, String reCaptchaVerifyUrl, String siteSecret) {
        this.restTemplate = restTemplate;
        this.reCaptchaVerifyUrl = reCaptchaVerifyUrl;
        this.siteSecret = siteSecret;
    }

    @Override
    public void verifyCaptcha(String value) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("secret", siteSecret);
        map.add("response", value);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(reCaptchaVerifyUrl, request, Map.class);

        Boolean success = (Boolean) response.getBody().get("success");

        if (success == null || !success) {
           throw new ReCaptchaVerificationFailedException();
        }
    }
}
