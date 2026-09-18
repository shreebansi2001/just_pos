package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftRequestDto {

	@NotBlank(message = "Name (English) is required")
	private String nameEnglish;
	
	private String nameHindi;
	
	private String nameGujarati;
	
	@NotBlank(message = "Shift Time is required")
	private String shifttime;

	private BigDecimal price;
	private Long userId;
	
}
