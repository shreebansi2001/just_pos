package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDishCostingResponseDto {
	
	private Double cheflaborcharge=0.0;
	private Double laborcharge=0.0;
	private Double outsideagencycharge=0.0;
	private Double extraexpensecharge=0.0;
	private Double rawmaterialcharge=0.0;
	private Integer pax = 0;
	private EventFunctionMasterResponseDto eventFunction;
	
//	private Double total=0.0;
//	private Double totalbyperplate=0.0;
	
}
