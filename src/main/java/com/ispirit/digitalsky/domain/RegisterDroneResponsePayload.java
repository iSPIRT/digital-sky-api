package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.util.CustomLocalDateTimeDeSerializer;
import com.ispirit.digitalsky.util.CustomLocalDateTimeSerializer;
import com.ispirit.digitalsky.util.LocalDateTimeAttributeConverter;

import javax.persistence.Convert;
import java.time.LocalDateTime;

public class RegisterDroneResponsePayload {

    private String txn;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime responseTimeStamp;

    private RegisterDroneResponseCode responseCode;

    private Errors error;

    public RegisterDroneResponsePayload () {
        this.responseTimeStamp = LocalDateTime.now();
    }

    public String getTxn() {
        return txn;
    }

    public void setTxn(String txn) {
        this.txn = txn;
    }

    public LocalDateTime getResponseTimeStamp() {
        return responseTimeStamp;
    }

    public void setResponseTimeStamp(LocalDateTime responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
    }

    public RegisterDroneResponseCode getResponseCode() { return responseCode; }

    public void setResponseCode(RegisterDroneResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public Errors getError() {
        return error;
    }

    public void setError(Errors error) {
        this.error = error;
    }
}
