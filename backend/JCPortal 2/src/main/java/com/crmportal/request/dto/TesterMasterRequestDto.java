package com.crmportal.request.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TesterMasterRequestDto {

	@NotNull(message = "Id is required.")
	private Long id;
	
	@NotNull(message = "Name is required.")
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
//	@NotNull(message = "Email is required.")
	private String email;
	
	@NotNull(message = "Contact No. is required.")
	private String contactNo;
	
	@NotNull(message = "User Id is required.")
	private Long userId;
	
	private String birthDate;
	
	private String aniversaryDate;
	
}
