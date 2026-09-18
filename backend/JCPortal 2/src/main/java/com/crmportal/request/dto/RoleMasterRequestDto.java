package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RoleMasterRequestDto {
	
	@NotBlank(message = "Role name is required")
	private String name;
	
	private Long userId;
	
	public String getName() {
        return name != null ? name.trim() : null;
    }

    public void setName(String name) {
        this.name = name;
    }
}
