package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionStaffTaskRequestDto {

	private Long taskId;
	private String taskName;
	private String taskNameHindi;
	private String taskNameGujarati;
	private String remarks;
	private Boolean isCompleted;
	private Boolean isCommonTask;
	private String completedDate;
}
