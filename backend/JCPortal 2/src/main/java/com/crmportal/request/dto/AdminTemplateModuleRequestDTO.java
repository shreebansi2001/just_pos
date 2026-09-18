package com.crmportal.request.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminTemplateModuleRequestDTO {

	@NotNull
	Long userId;
	
	@NotNull
	Long templateMasterId;
	
	@NotNull
	Long templateModuleMasterId;
}
