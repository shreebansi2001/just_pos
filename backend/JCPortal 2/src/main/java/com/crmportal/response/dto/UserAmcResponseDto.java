package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.mail.Multipart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAmcResponseDto {
	private Long id;
	
	private String amcType;

	private BigDecimal amcAmount;

	private String amcRemarks;
	
	private String amcDate;

	private BigDecimal amcRecivableAmount;

	private String amcRecivableDate;

	private String status;

	private String file;
	
	private Long userId;
}
