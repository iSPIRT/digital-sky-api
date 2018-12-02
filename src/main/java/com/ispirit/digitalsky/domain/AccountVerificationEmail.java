package com.ispirit.digitalsky.domain;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccountVerificationEmail implements EmailMessage {

    private String templateName = "email_account_verification_template.ftl";
    private String subject = "[Digital Sky] Account Verification";
    private String to;
    private String resetPasswordLink;

    public AccountVerificationEmail(String to, String resetPasswordLink) {
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
        templateParams.put("accountVerificationLink", resetPasswordLink);
        return templateParams;
    }

    @Override
    public Map<String, File> attachments() {
        return Collections.emptyMap();
    }
}
