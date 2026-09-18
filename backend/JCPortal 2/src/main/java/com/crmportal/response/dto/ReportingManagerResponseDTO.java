package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportingManagerResponseDTO {

	private Long id;
	private String name;
	private String email;
	private String contactNo;
	private String pre_fix;
	private String createdAt;
	private Boolean isApprove;
	private String companyName;
}
