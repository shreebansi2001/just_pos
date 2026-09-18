package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorPaymentInvoiceResponseDto {

	private Long id;
	private String invoiceCode;
	private String date;
	private String payMode;
	private Long bankId;
	private String bankName;
	private Long cashId;
	private String cashName;
	private String referenceId;
	private BigDecimal payAmount;
	private Long vendorId;
	private String vendorName;
	private String remarks;
	private BigDecimal receivedAmount;
	private Boolean isPayable;
	private BigDecimal settlementAmount;
	private Long eventId;
	private String eventName;
	private Boolean isOpb;
}
