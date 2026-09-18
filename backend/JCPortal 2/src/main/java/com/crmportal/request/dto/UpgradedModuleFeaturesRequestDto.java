package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpgradedModuleFeaturesRequestDto {

	private Long id;
	private String featureText;
}
