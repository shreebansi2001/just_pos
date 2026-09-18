package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWiseStoreIssuePricing {

	private Long eventId;
	
	private BigDecimal totalIsusePrice;
	
	private BigDecimal totalReturnPrice;
	
	private BigDecimal totalPrice;
}
