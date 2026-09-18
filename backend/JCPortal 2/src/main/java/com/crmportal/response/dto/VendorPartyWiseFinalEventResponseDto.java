package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorPartyWiseFinalEventResponseDto {

	private BigDecimal totalAmnt;
	
	List<VendorPartyWiseEventResponseDto> partyWiseEvents;
}
