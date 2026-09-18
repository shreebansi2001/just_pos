package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicesDetailsResponseDto {

	private Long invoiceId;
    private String invoiceCode;
    private String billingName;
    private String billingAddress;
    private BigDecimal totalAmount;
    private LocalDate invoiceDate;

    private Long customerId;
    private String customerName;

    private List<InvoicePaymentDto> payments;
}
