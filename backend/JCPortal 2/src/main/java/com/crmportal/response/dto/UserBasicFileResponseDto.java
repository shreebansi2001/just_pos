package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBasicFileResponseDto {

	private Long userId;
	private String moduleName;
	private Long moduleRecordId;
	private String fileType;
}
