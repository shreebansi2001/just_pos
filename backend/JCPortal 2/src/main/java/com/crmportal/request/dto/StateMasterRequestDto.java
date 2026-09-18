package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class StateMasterRequestDto {

	@NotBlank(message = "State Name is required")
	private String name;
	
	@NotNull(message = "Please provide Country Id")
    private Long countryId;
	
	public String getName() {
        return name != null ? name.trim() : null;
    }

    public void setName(String name) {
        this.name = name;
    }
}
