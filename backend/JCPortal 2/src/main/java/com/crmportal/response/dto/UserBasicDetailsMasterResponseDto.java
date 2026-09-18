package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicDetailsMasterResponseDto {

	private Long id;
	private String companyName;
	private String countryCode;
	private String companyEmail;
	private String officeNo;
	private String address;
	private Boolean isAttendanceLeaveAccess;
	private Boolean isTaskAccess;
	private Long reportingManagerId;
	private CountryMasterResponseDto country;
	private StateMasterResponseDto state;
	private CityMasterResponseDto city;
	private RoleMasterResponseDto role;
	private String type;
	private String services;
}
