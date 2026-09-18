package com.crmportal.response.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameGujarati;
	private String nameHindi;
	private Boolean isDelete;
	private Boolean isActive;
	private String createdAt;
	private String venueImg;
	private Long userId;

}
