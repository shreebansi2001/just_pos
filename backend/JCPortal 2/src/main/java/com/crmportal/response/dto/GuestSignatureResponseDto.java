package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestSignatureResponseDto {

	private Long id;
	
	private Long eventId;
	
	private Long eventFunctionId;
	
	private String particulars;
	
	private Integer persons;
	
	private Integer extra;
	
	private String createdAt;
	
	private String updatedAt;
	
	private Long userId;
}
