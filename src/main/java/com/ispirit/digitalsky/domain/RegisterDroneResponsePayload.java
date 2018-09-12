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

    private RegisterDroneResponseCode code;

    private Errors error;

    public RegisterDroneResponsePayload () {

    }

    public RegisterDroneResponsePayload(String txn, LocalDateTime responseTimeStamp) {
        this.txn = txn;
        this.responseTimeStamp = responseTimeStamp;
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

    public RegisterDroneResponseCode getCode() { return code; }

    public void setCode(RegisterDroneResponseCode code) {
        this.code = code;
    }

    public Errors getError() {
        return error;
    }

    public void setError(Errors error) {
        this.error = error;
    }
}
