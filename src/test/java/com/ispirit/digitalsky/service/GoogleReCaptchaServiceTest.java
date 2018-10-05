package com.ispirit.digitalsky.service;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleReCaptchaServiceTest {

    private RestTemplate restTemplate;

    @Test
    public void shouldVerifyCaptcha() throws Exception {
        //given
        restTemplate = mock(RestTemplate.class);
        String url = "http://localhost";
        String secret = "ds";
        GoogleReCaptchaService service = new GoogleReCaptchaService(restTemplate, url, secret);
        ArgumentCaptor<HttpEntity> argumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        Map response = new HashMap();
        response.put("success", Boolean.TRUE);
        when(restTemplate.postForEntity(eq(url), argumentCaptor.capture(), eq(Map.class)))
                .thenReturn( new ResponseEntity<Map>(response, HttpStatus.OK));
        //when
        service.verifyCaptcha("captchaValue");

        //then
        Map body = (Map) argumentCaptor.getValue().getBody();
        assertThat(((List) body.get("secret")).get(0), is(secret));
        assertThat(((List) body.get("response")).get(0), is("captchaValue"));

        assertThat(argumentCaptor.getValue().getHeaders().getContentType(), is(MediaType.APPLICATION_FORM_URLENCODED));

    }
}