package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.request.dto.SpecialNotesImagesRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialNotesResponseDto {

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
	private List<SpecialNotesImagesResponseDto> files;
}
