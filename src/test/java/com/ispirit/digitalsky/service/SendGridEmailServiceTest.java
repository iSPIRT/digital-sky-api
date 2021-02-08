package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.AccountVerificationEmail;
import com.ispirit.digitalsky.domain.EmailMessage;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SendGridEmailServiceTest {

    private SendGridEmailService service;
    private SendGrid sendGrid;

    @Before
    public void setUp() throws Exception {
        sendGrid = mock(SendGrid.class);
        service = new SendGridEmailService(sendGrid, freemarkerConfiguration(), "no-reply@digitalsky.com");
    }

    @Test
    public void shouldSendEmail() throws Exception {
        //given
        EmailMessage emailMessage = new AccountVerificationEmail("to@sample.com", "/link");
        when(sendGrid.api(any())).thenReturn(new Response(202,"", new HashMap<>()));

        //when
        service.send(emailMessage);

        //then
        ArgumentCaptor<Request> argumentCaptor = ArgumentCaptor.forClass(Request.class);
        verify(sendGrid).api(argumentCaptor.capture());

        String expected = "{\"from\":{\"email\":\"no-reply@digitalsky.com\"},\"subject\":\"[Digital Sky] Account Verification\",\"personalizations\":[{\"to\":[{\"email\":\"to@sample.com\"}]}],\"content\":[{\"type\":\"text/plain\",\"value\":\"\r\nDear Sir/Madam,\r\n\r\nPlease use following link to verify your account.\r\n\r\n/link\r\n\r\nThanks\r\nDigiSky Team\r\n\r\n\"}]}";

        String received = argumentCaptor.getValue().getBody();
        String received_formatted = received.replace("\r", "");
        String expected_formatted = received.replace("\r", "");

        assertThat(received_formatted.length(), is(expected_formatted.length()));
    }

    public freemarker.template.Configuration freemarkerConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_20);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocalizedLookup(false);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return cfg;
    }
}