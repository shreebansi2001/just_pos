package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventGeneralFixRawRequestDto {
	private Long id;
	private Long rawCatId;
	private Long unitId;
	private Long rawId;
	private BigDecimal weight;
	private BigDecimal price;
	
	private List<EventFunctionGeneralFixRawRequestDto> eventFunctionGeneralFixRaws;
}
