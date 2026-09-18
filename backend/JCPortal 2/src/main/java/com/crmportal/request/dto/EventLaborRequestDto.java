package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLaborRequestDto {

	private Long eventId;
	private Long eventFunctionId;
	private List<EventLaborDetailsRequestDto> eventLaborDetails;
}
