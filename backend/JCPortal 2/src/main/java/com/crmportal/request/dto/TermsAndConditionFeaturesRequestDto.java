package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermsAndConditionFeaturesRequestDto {

	private String description;
	private String description_hindi;
	private String description_gujarati;
}
