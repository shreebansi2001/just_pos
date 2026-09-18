package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWeightRateCalRequestDto {

	private Long menuItemId;
	private Long eventId;
	private Long eventFunctionId;
	private Boolean isPaxChange;
	private Integer personCount;
	private Long userId;
	private Boolean isOutSide;
}
