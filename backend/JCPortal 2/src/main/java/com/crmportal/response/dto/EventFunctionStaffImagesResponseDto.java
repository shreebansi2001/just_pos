package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionStaffImagesResponseDto {

	private Long uploadId;
	private Long assignmentId;
	private String path;
}
