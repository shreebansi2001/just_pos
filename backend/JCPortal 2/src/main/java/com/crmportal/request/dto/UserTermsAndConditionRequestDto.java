package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTermsAndConditionRequestDto {

	private Long id;
	private String nameEnglish;
	private String nameGujarati;
	private String nameHindi;
	private Long userId;
	private List<TermsAndConditionFeaturesRequestDto> termsAndConditionFeatures;
}
