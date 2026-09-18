package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRemarkMasterRequestDto {

	@NotBlank(message = "Name (English) is required")
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
	private String type;
	
	private Boolean isOdc;
	
	private Boolean isActive = true;  // default true
	
	@NotNull(message = "User Id is required")
	private Long userId;
}
