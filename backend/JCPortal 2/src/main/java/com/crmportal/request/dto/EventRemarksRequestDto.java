package com.crmportal.request.dto;

import lombok.Data;

@Data
public class EventRemarksRequestDto {

	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Long eventId;
}
