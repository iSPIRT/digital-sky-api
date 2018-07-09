package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.EmailMessage;

public interface EmailService   {

    void send(EmailMessage emailMessage);
}
