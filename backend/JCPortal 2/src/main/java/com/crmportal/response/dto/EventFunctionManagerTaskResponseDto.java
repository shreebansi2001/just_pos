package com.crmportal.response.dto;

import java.util.List;

import javax.persistence.Column;

import com.crmportal.enums.PriorityType;
import com.crmportal.enums.TaskType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionManagerTaskResponseDto {

	private Long id;
	private Long managerTaskId;
	private String name;
	private String description;
	private Long managerId;
	private Long eventFunctionId;
	private Long eventId;
	private String remarks;
	private String latitude;
	private String longitude;
	private String status;
	private String completedAt;
	private String priority;
	private String type;
	private List<SpecialNotesImagesResponseDto> files;
}
