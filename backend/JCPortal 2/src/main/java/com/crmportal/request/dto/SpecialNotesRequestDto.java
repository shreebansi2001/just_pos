package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialNotesRequestDto {

	private Long id;
	private String name;
	private String description;
	private Long userId;
	private Long managerId;
	private String remarks;
	private String priority;
	private String status;
	private Long eventId;
	private Long eventFunctionId;
	private Integer sequence;
	private List<SpecialNotesImagesRequestDto> files;
}
