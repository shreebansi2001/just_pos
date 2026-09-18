package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.request.dto.SalesInvoiceRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesInvoiceResponseDto {

	private Long id;
	
	private String invoiceNo;
	
	private String paymentDate;
	
	private BigDecimal invoiceAmount;
	
	private BigDecimal dueAmount;
	
	private BigDecimal totalAmount;
	
	private String status;
	
	private String paymentMode;
	
	private String reference;
	
	private Long bankId;
	
	private Long cashId;
	
	private AccountType accountType;
	
	private Long userId;
	
	private Long eventId;
}
