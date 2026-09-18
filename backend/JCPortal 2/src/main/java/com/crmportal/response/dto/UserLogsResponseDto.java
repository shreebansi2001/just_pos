package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLogsResponseDto {

	private Long userId;
	private String userName;
	private String companyName;
	private String email;
	private String status;
}
