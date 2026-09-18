package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.crmportal.entity.InvoiceEntity;
import com.crmportal.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePaymentHistoryResponseDto {

	private Long invoicePaymentHistoryId;
	
	private Long invoiceId;
	
	private String invoiceCode;
	
	private BankDetailsResponseDto bankDetails;
	
	private String transactionId;
	
	private String payment_date;
	
	private String chequeNo;
	
	private BigDecimal amount;
	
	private BigDecimal dueAmount;

	private PaymentMode paymentMode;
	
	private String status;
	
	private Boolean isDelete;
	
	private String createdAt;

    private String updatedAt;
    
}
