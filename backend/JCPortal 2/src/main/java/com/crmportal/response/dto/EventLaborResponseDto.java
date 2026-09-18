package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.request.dto.EventLaborDetailsRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLaborResponseDto {

	private Long eventId;
	private Long eventFunctionId;
	private List<EventLaborDetailsResponseDto> eventLabor;
}
