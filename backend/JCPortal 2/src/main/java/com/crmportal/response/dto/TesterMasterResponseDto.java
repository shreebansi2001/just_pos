package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TesterMasterResponseDto {

	private Long id;
	
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
	private String email;
	
	private String contactNo;
	
	private String birthDate;
	
	private String aniversaryDate;
}
