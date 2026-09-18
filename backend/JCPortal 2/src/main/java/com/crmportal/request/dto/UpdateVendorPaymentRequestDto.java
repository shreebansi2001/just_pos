package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVendorPaymentRequestDto {

	private Long id;
	private Long vendorId;
	private String vendorCat;
	private String paymentDate;
	private String paymentMode;
	private Long bankId;
	private Long cashId;
	private String referenceId;
	private Long userId;
	private String remarks;
	private Boolean isPayable;
	private Boolean isOpb;
	private BigDecimal payAmount;
	private BigDecimal settlementAmount;
	private BigDecimal receivedAmount;
	private Long eventId;
	
}
