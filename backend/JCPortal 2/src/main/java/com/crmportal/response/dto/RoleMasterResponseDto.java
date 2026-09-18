package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleMasterResponseDto {
	
	private Long id;
	private String name;
	private String createdAt;
	private Long userId;
	public RoleMasterResponseDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
}
