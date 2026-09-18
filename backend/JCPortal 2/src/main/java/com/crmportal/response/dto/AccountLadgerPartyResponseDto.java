package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLadgerPartyResponseDto {

	private Long partyId;
	private String partyName;
	private String eventName;
	private String eventStartDate;
	private String eventEndDate;
	private String type;
	private String opbDate;
	private BigDecimal opb;
}
