package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FnDetailsForDatewiseReportResponseDto {

	private String fnStartDate;
	private String venue;
	private List<MenuItemForDatewiseReportResponseDto> menuItems;
}
