package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateAIRequestDto {

	private Long userId;
	private Long aiTemplateId;
	private Long packageId;
	private String functionName;
	private String eventFunctionIds = "";
	private String priceRange = "";
}
