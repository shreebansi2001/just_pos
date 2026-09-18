package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpgradedModuleResponseDto {

	private Long id;
	private Long upgradeModuleId;
	private String moduleName;
	private String billingCycle;
	private BigDecimal payAmount;
	private Boolean isPayDone;
	private Boolean isActive;
	private String startDate;
	private String endDate;
	private Long userId;
}
