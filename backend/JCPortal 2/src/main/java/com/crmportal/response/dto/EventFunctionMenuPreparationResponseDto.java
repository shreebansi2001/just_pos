package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuPreparationResponseDto {

	private Long id;

	private Long funcId;
	
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
	private String functionStartDateTime;

	private String functionEndDateTime;

	private Integer pax;

	private Double rate;

	private String function_venue;

	private String notesEnglish;

	private String notesHindi;

	private String notesGujarati;
	
	private Long eventId;
	
	private String createdAt;
	
	private Long    customPackageId;
	private String  customPackageName;
	private Boolean isPackage;
}
