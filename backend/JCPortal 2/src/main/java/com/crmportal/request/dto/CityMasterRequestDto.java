package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


import lombok.Data;

@Data
public class CityMasterRequestDto {

	 	@NotBlank(message = "City Name is required")
	    private String name;

	    @NotNull(message = "Please provide State Id!")
	    private Long stateId;
	    
	    public String getName() {
	        return name != null ? name.trim() : null;
	    }

}
