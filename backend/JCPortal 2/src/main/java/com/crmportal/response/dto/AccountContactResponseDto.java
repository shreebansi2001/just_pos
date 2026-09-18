package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.crmportal.enums.EntryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountContactResponseDto {

	private Long accountContactId;
	
	private String name;
	
	private BigDecimal openingBalance;
	
	private BigDecimal currentBalance;
	
	private String openingDate;
	
	private EntryType entryType;
	
	private Long userId;
	
	private Long memberId;
	
	private Boolean isDelete;
	
	private String createdAt;
	
	private String updatedAt;
}
