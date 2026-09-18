package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLabourOverviewResponse {

	private String staffCategory;

	private String shift;

	private Integer assignedQty;

	private Integer confirmedQty;
}