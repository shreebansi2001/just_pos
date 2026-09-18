package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionWiseManagerTaskResponseDto {

	private Long eventFunctionId;
	private Long managerId;
	private Long managerTaskId;
	private Long eventFunctionManagerTaskId;
}
