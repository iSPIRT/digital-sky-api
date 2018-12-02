package com.ispirit.digitalsky.domain;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResetPasswordEmail implements EmailMessage {

    private String templateName = "email_reset_password_template.ftl";
    private String subject = "[Digital Sky] Reset password link";
    private String to;
    private String resetPasswordLink;

    public ResetPasswordEmail(String to, String resetPasswordLink) {
        this.to = to;
        this.resetPasswordLink = resetPasswordLink;
    }

    @Override
    public String to() {
        return to;
    }

    @Override
    public String subject() {
        return subject;
    }

    @Override
    public String templateName() {
        return templateName;
    }

    @Override
    public Map<String, String> templateParameters() {
        HashMap<String, String> templateParams = new HashMap<>();
        templateParams.put("resetPasswordLink", resetPasswordLink);
        return templateParams;
    }

    @Override
    public Map<String, File> attachments() {
        return Collections.emptyMap();
    }
}
