package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryMasterResponseDto {

	private Long id;
	private String name;
	private String code;
	private String createdAt;
	public CountryMasterResponseDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
	
}
