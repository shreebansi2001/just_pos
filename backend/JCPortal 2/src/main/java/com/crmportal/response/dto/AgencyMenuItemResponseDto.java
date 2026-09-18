package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyMenuItemResponseDto {

	private Long menuItemId;
	private String menuItemNameEnglish;
	private String menuItemNameHindi;
	private String menuItemNameGujarati;
	private Integer pax;
}
