package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerTaskMasterResponseDto {

	private Long id;
	private String name;
	private String description;
	private Long userId;
	private String priority;
	private String type;
	private Integer sequence;

}
