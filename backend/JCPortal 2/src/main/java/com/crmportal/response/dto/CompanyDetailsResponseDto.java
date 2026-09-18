package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetailsResponseDto {

	private String companyName;
	private String countryCode;
	private String companyEmail;
	private String officeNo;
	private String address;
	private String logo;
}
