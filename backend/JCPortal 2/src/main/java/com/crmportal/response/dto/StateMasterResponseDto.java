package com.crmportal.response.dto;

import com.crmportal.entity.CountryMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateMasterResponseDto {
	
	private Long id;
	private String name;
	private String CreatedAt;
	private CountryMasterResponseDto country;
	public StateMasterResponseDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
}
