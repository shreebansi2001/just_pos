package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AllVendorsPaymentResponseDto {

	private String vendorCategory;
	private Long vendorId;
	private String vendorName;
	private BigDecimal totalAmt;
	private BigDecimal paidAmt;
	private BigDecimal pendingAmt;

	// 👇 Custom constructor for JPQL
	public AllVendorsPaymentResponseDto(String vendorCategory, Long vendorId, String vendorName, Number totalAmt,
			Number paidAmt, Number pendingAmt) {

		this.vendorCategory = vendorCategory;
		this.vendorId = vendorId;
		this.vendorName = vendorName;

		this.totalAmt = totalAmt != null ? new BigDecimal(totalAmt.toString()) : BigDecimal.ZERO;
		this.paidAmt = paidAmt != null ? new BigDecimal(paidAmt.toString()) : BigDecimal.ZERO;
		this.pendingAmt = pendingAmt != null ? new BigDecimal(pendingAmt.toString()) : BigDecimal.ZERO;
	}
}