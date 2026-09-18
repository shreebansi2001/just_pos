package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.crmportal.enums.EntryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountContactRequestDto {

	@NotNull(message = "Account contact ID is required. Use -1 to create a new record or provide an existing ID to update.")
	private Long accountContactId;

	@NotNull(message = "Name is required")
	private String name;

	@NotNull(message = "Opening balance is required")
	private BigDecimal openingBalance;

	@NotNull(message = "Current balance is required")
	private BigDecimal currentBalance;

	@NotNull(message = "Opening date is required")
	private String openingDate;

	@NotNull(message = "Entry type is required.")
	private EntryType entryType;
	
	private Long memberId;
	
	@NotNull(message = "User id is required.")
	private Long userId;
	
}
