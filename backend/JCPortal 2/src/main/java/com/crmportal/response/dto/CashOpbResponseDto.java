package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.crmportal.request.dto.ContactTypeMasterRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashOpbResponseDto {

	private Long id;
	
	private String accountName;
	
	private ContactTypeMasterResponseDto contactType;
	
	private BigDecimal openingBalance;
	
	private BigDecimal currentBalance;
	
	private String description;
	
	private Long userId;
	
	private Boolean isPrimary;
	
	private Boolean isDelete;

	private String createdAt;
	
	private String updatedAt;
}
