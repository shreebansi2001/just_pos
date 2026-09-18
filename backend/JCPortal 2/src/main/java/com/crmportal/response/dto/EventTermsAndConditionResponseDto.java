package com.crmportal.response.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.crmportal.request.dto.EventTermsAndConditionFeaturesRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTermsAndConditionResponseDto {

	private Long id;
	
	private String nameEnglish;
	
	private String nameHindi;

	private String nameGujarati;
	
	private Long eventId;
	
	private Long userId;
	
	private List<EventTermsAndConditionFeaturesRequestDto> features;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	private Boolean isDelete;
}
