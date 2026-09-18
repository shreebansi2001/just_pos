package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpgradedModuleResponseDto {
	
		private Long id;
		private String moduleName;
		private BigDecimal price;
		private String description;
		private Boolean isActive;
		private String createdAt;
		private String billingCycle;
		private Boolean isPurchase;
		private Boolean isConfig;
		private Long uncId;
		private String key1;
		private String key2;
		private String url;
		private String startDate;
		private String endDate;
		private List<UpgradedModuleFeaturesResponseDto> upgradedModuleFeatures;
}
