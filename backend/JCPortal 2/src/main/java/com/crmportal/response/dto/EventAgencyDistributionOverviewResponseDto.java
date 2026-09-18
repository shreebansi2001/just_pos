package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventAgencyDistributionOverviewResponseDto {

	private String categoryName;
	private String vendorName;
	private String shifDateTime;
	private String shiftName;
	private BigDecimal qty;
	private BigDecimal rate;
	private BigDecimal totalRate;
}
