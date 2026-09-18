package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorPartyWiseEventResponseDto {

	private Long eventId;
	private String eventName;
	private BigDecimal remaingAmnt;
	private String eventDate;
}
