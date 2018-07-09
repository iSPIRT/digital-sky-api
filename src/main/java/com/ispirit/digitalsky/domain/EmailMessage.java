package com.ispirit.digitalsky.domain;

import java.io.File;
import java.util.Map;

public interface EmailMessage {

    String to();

    String subject();

    String templateName();

    Map<String, String> templateParameters();

    Map<String, File> attachments();
}
