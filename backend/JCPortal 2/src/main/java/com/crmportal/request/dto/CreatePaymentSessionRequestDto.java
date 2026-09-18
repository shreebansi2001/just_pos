package com.crmportal.request.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreatePaymentSessionRequestDto {

    private BigDecimal amount;      // 100.5
    private String currency;         // INR
    private Integer expiresIn;       // 900
    private String description;
    private String invoiceNumber;
    private String referenceNumber;
    private List<MetaDataDto> metaData;

    @Data
    public static class MetaDataDto {
        private String key;
        private String value;
    }
}
