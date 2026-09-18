package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventGroundTaskResponseDto {

	private Long id;

	private String resourceType;

	private String nameEnglish;

	private String nameHindi;

	private String nameGujarati;

	private Boolean isDelete;

	private Boolean isTrue;

	private Long userId;

}
