package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationConfigResponseDto {
		
	private Long id;
	private String key1;
	private String key2;
	private String url;
	private Boolean isActive;
	private Boolean isDelete;
	private Boolean isPayDone;
	private Long moduleId;
	private String moduleName;
	private String startDate;
	private String endDate;
	private Long userId;
	private BigDecimal payAmount;
	
}
