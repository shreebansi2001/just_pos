package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionStaffTaskResponseDto {

	private Long taskId;
	private String taskName;
	private String taskNameHindi;
	private String taskNameGujarati;
	private Boolean isCompleted;
	private Boolean isCommonTask;
	private String remarks;
}
