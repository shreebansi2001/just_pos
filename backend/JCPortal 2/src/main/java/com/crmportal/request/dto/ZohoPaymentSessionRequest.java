package com.crmportal.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ZohoPaymentSessionRequest {

    private BigDecimal amount;
    private String currency;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    private String description;

    @JsonProperty("invoice_number")
    private String invoiceNumber;

    @JsonProperty("reference_number")
    private String referenceNumber;

    @JsonProperty("meta_data")
    private List<MetaData> metaData;

    @Data
    public static class MetaData {
        private String key;
        private String value;
    }
}
