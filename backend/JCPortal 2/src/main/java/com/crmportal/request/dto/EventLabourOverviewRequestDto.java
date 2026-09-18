package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLabourOverviewRequestDto {
	private String staffCategory;
	private String shift;
	private Integer confirmedQty;
	private Integer assignedQty;
}
