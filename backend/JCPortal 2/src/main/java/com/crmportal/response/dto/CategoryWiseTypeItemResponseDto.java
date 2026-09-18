package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWiseTypeItemResponseDto {

	private Long menuItemId;
	
	private String menuItemName;
	
	private String instruction;
}
