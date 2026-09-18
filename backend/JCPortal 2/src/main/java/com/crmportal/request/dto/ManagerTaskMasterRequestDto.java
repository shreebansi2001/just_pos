package com.crmportal.request.dto;

import javax.persistence.Column;

import com.crmportal.enums.PriorityType;
import com.crmportal.enums.TaskType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerTaskMasterRequestDto {

	private Long id;
	private String name;
	private String description;
	private Long userId;
	private String priority;
	private String type;
	private Integer sequence;
}
