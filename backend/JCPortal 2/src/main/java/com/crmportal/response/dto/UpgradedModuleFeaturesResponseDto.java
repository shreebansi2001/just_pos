package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpgradedModuleFeaturesResponseDto {

	private Long id;
	private String featureText;
	private Long upgradedModuleId;
	private String upgradedModuleName;
	private String createdAt;
}
