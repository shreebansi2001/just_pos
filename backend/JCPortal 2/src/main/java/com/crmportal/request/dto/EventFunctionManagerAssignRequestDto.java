package com.crmportal.request.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionManagerAssignRequestDto {
	private Long eventId;
	private Long userId;
	private List<EventFunctionManagerAssignListRequestDto> eventFunctionManagers;
}
