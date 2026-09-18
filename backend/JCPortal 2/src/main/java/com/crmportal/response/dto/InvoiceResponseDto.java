package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDto {

	private Long id;
	
	private String partyName;

	private String planName;

	private String planStartDate;

	private String planEndDate;

	private String dueDate;

	private String billingAddress;

	private String shippingAddress;

	private String billingName;

	private String gstNumber;

	private List<PlanInformationResponseDto> planInformation;

	private String notes;

	private BigDecimal subTotal;

	private BigDecimal cgstPercentage;

	private BigDecimal cgstAmount;

	private BigDecimal sgstPercentage;

	private BigDecimal sgstAmount;

	private BigDecimal igstPercentage;

	private BigDecimal igstAmount;

	private BigDecimal discount;

	private BigDecimal roundOff;

	private BigDecimal grandTotal;
	
	private Long userId;

	
}
