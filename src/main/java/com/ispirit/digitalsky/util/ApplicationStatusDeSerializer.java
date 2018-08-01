package com.ispirit.digitalsky.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.ispirit.digitalsky.domain.ApplicationStatus;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.ValidationException;

import java.io.IOException;

public class ApplicationStatusDeSerializer extends StdDeserializer<ApplicationStatus> {

    protected ApplicationStatusDeSerializer() {
        this(null);
    }

    protected ApplicationStatusDeSerializer(Class<ApplicationStatus> t) {
        super(t);
    }

    @Override
    public ApplicationStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(jsonParser.getText());
        if(applicationStatus == ApplicationStatus.APPROVED || applicationStatus == ApplicationStatus.REJECTED){
            throw new ValidationException(new Errors("Invalid Application Status Value"));
        }
        return applicationStatus;
    }

}
