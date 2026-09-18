package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpgradedModuleRequestDto {

	
	private Long id;
	
	private BigDecimal price;
	
	private String moduleName;

	private String description;
	
	private String billingCycle;
	
	private Boolean isConfig;
	
	private List<UpgradedModuleFeaturesRequestDto> upgradedModuleFeatures;
}
