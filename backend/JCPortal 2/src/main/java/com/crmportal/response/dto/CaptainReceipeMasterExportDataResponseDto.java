package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeMasterExportDataResponseDto {

	private String name;
	
	private BigDecimal weight;
	
	private BigDecimal rate;
	
	private String unitName;
}
