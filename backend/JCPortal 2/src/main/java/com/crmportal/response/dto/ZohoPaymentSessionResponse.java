package com.crmportal.response.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZohoPaymentSessionResponse {

    private int code;
    private String message;

    @JsonProperty("payments_session")
    private Object paymentsSession;
}