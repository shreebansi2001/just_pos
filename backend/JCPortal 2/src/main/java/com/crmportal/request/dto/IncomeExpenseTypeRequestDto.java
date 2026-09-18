package com.crmportal.request.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomeExpenseTypeRequestDto {

	@NotNull(message = "Type id is required.")
	private Long typeId;
	
	@NotNull(message = "Name is required.")
	private String name;
	
	@NotNull(message = "Type is required.")
	private String type;
	
	@NotNull(message = "User id is required.")
	private Long userId;
	
}
