package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoupenMasterRequestDto {
	
	private Long id;
	
	private Integer maxUser;
	
	private String coupenName;

	private String coupenCode;
	
	private BigDecimal price;
	
	private String expireDate;
}
