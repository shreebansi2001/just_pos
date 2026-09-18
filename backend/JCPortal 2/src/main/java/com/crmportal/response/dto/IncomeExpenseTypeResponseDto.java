package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeExpenseTypeResponseDto {

	private Long typeId;
	
	private String name;
	
	private String type;
	
	private Long userId;
	
	private String createdAt;
	
	private String updatedAt;
}
