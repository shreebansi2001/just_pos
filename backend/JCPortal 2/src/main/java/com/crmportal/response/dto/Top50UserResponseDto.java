package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Top50UserResponseDto {

	private Long userId;
	private String userName;
	private String companyName;
	private String createdAt;
	private String status;
}
