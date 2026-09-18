package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryMasterRequestDto {

	@NotBlank(message = "City Name is required")
    private String name;

	@NotBlank(message = "City Code is required")
    private String code;
    
    public String getName() {
        return name != null ? name.trim() : null;
    }
    
    public String getCode() {
        return code != null ? code.trim() : null;
    }
}
