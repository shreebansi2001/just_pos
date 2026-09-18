package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.request.dto.TermsAndConditionFeaturesRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTermsAndConditionResponseDto {

	private Long id;
	private Boolean isActive;
	private Boolean isDelete;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Long userId;
	private String createdAt;
	private List<TermsAndConditionFeaturesRequestDto> features;
}
