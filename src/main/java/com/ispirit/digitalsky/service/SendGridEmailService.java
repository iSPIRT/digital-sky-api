package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.EmailMessage;
import com.ispirit.digitalsky.service.api.EmailService;
import com.sendgrid.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Map;

import static java.lang.String.format;

public class SendGridEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    private static final String MAIL_SEND_ENDPOINT = "mail/send";

    private SendGrid sendGrid;
    private Configuration configuration;
    private String defaultFromAddress;

    public SendGridEmailService(SendGrid sendGrid, Configuration configuration, String defaultFromAddress) {
        this.sendGrid = sendGrid;
        this.configuration = configuration;
        this.defaultFromAddress = defaultFromAddress;
    }

    @Override
    public void send(EmailMessage emailMessage) {
        try {
            Mail mail = buildMail(emailMessage);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint(MAIL_SEND_ENDPOINT);
            request.setBody(mail.build());

            Response response = sendEmail(request);
            int statusCode = response.getStatusCode();
            logger.info(format("Successfully sent email with subject %s to %s", emailMessage.subject(), emailMessage.to()));
            if (statusCode != HttpStatus.ACCEPTED.value()) {
                logger.error(format("Error while sending Email with subject %s to %s", emailMessage.subject(), emailMessage.to()));
                logger.error(response.getBody());
                logger.error(response.getHeaders().toString());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    Response sendEmail(Request request) throws IOException {
        return sendGrid.api(request);
    }

    private Mail buildMail(EmailMessage emailMessage) throws IOException, TemplateException {
        Email from = new Email(defaultFromAddress);
        Email to = new Email(emailMessage.to());
        Content content = new Content("text/plain", getEmailContent(emailMessage));

        Mail mail = new Mail(from, emailMessage.subject(), to, content);

        Map<String, File> attachmentMap = emailMessage.attachments();

        for (String fileName : attachmentMap.keySet()) {
            File file = attachmentMap.get(fileName);
            if (file.exists()) {
                byte[] fileContent = FileUtils.readFileToByteArray(file);
                Attachments attachments = new Attachments();
                attachments.setFilename(fileName);
                attachments.setContent(Base64.getEncoder().encodeToString(fileContent));
                mail.addAttachments(attachments);
            }
        }
        return mail;
    }

    private String getEmailContent(EmailMessage emailMessage) throws IOException, TemplateException {
        Template template = configuration.getTemplate(emailMessage.templateName());
        StringWriter stringWriter = new StringWriter();
        template.process(emailMessage.templateParameters(), stringWriter);
        return stringWriter.toString();
    }
}
