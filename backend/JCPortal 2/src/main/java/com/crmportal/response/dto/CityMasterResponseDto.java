package com.crmportal.response.dto;

import lombok.Data;

@Data
public class CityMasterResponseDto {

	private Long id;
	private String name;
	private String createdAt;
	private StateMasterResponseDto state;
	public CityMasterResponseDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
}
