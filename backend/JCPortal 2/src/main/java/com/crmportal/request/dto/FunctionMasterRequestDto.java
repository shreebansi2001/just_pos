package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionMasterRequestDto {

	@NotBlank(message = "Name (English) is required")
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
	@NotBlank(message = "Start Time is required")
	private String startTime;
	
	@NotBlank(message = "Start End is required")
	private String endTime;
	
	private Long userId;
	
}
