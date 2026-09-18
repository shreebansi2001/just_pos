package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLaborShiftDetailsResponseDto {

	private String laborshift;
	private String labordatetime;
	private BigDecimal shiftTranPrice;
	private Double price=0.0;
	private Double qty=0.0;
	private Double totalprice=0.0;
	private String place;
	private String notesEnglish;
	private String notesHindi;
	private String notesGujarati;
}
