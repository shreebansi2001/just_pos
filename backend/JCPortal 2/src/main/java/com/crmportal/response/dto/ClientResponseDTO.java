package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseDTO {

	private Long id;
	private String name;
	private String email;
	private String contactNo;
	private String pre_fix;
}
