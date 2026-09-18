package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLaborDetailsRequestDto {

	private Long labortypeid;
	private Long contactid;
	private Integer sortOrder;
	List<EventLaborShiftDetailsRequestDto> labourShift;
}
