package com.crmportal.request.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestSignatureRequestDto {

	@NotNull(message = "Id is required.")
	private Long id;
	
	@NotNull(message = "Particulars is required.")
	private String particulars;
	
	@NotNull(message = "Persons is required.")
	private Integer persons;
	
	private Integer extra = 0;
	
	private Long userId;
}
