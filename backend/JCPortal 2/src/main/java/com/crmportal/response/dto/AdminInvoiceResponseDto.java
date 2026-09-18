package com.crmportal.response.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminInvoiceResponseDto {

	private Long id;
	
	private String userName;

	private String planName;

	private BigDecimal grandTotal;
	
	private BigDecimal totalPaid;
	
	private BigDecimal dueBalance;
}
