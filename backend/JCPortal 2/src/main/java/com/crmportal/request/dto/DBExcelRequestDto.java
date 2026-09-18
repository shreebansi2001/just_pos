package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class DBExcelRequestDto {
	@NotBlank(message = "dbName is required")
	private String dbName;

	@NotNull(message = "state is required")
	private String state;
	

	@NotNull(message = "userId is required")
	private String userId;

	@NotNull(message = "insturctions are required")
	private String instructions;
}