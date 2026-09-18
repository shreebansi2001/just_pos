package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTermsAndConditionRequestDto {

	private Long id;
	
	private String nameEnglish;
	
	private String nameHindi;

	private String nameGujarati;
	
	private Long eventId;
	
	private Long userId;
	
	private List<EventTermsAndConditionFeaturesRequestDto> features;
}
