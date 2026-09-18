package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.crmportal.entity.InvoiceEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentMonthPaidInvoicesResponseDto {

	private InvoiceEntity invoice;
	
	private BigDecimal paidAmount;
}
