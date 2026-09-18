package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashOpbRequestDto {

	@NotNull(message = "Account name is required.")
	private String accountName;
	
	@NotNull(message = "Contact type is required.")
	private Long contactTypeId;
	
	@NotNull(message = "Opening balance is required.")
	private BigDecimal openingBalance;
	
	@NotNull(message = "Current balance is required.")
	private BigDecimal currentBalance;
	
	private String description;

	@NotNull(message = "User id is required.")
	private Long userId;
	
	@NotNull(message = "Is Primary is required.")
	private Boolean isPrimary;
}
