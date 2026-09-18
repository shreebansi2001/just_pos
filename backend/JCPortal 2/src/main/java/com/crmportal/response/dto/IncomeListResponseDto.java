package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeListResponseDto {

    private String clientName;
    private Long recordId;
    private String voucherNo;
    private BigDecimal amount;
    private String date;
    private PaymentMode paymentMode;
    
}
