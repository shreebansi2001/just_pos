package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InActiveUserResponseDto {

	private String userName;
	private String companyName;
	private String mobileNo;
	private String emailid;
	private String createdAt;
}
