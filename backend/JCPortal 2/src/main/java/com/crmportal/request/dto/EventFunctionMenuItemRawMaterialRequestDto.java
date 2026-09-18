package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionMenuItemRawMaterialRequestDto {

	private Long id;
	private BigDecimal weight;
	private BigDecimal rate;
    private BigDecimal rawmaterial_rate;
    private BigDecimal rawmaterial_weight;
	private String dateTime;
	private String place;
    private Long  partyId;
    private Long unitId;
	private Long rawMaterialId;
    private Long menuItemId;
    private Long eventId;
    private Long eventFunctionId;
    private Boolean isNewRaw;
    private Long masterRawUnitId;
    private BigDecimal supRate;
    private Boolean isCaptainReceipe;
}
