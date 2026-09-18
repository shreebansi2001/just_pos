package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralFixRawResponseDto {
	private Long eventId;
	private List<EventGeneralFixRawResponseDto> eventGeneralFixRaws;
}
