package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.crmportal.enums.EExpense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseManagementResponseDto {

	private Long expenseId;
	
	private String name;
	
	private BigDecimal amount;

	private String date;
	
	private String mobileNo;

	private String paymentType;

	private String remark;
	
	private String description;

	private String createdAt;
	
	private String countryCode;
	
	private String gstin;

	private String buildingAddress;

	private String area;

	private String pincode;

	private String city;

	private String state;
	
	private EExpense userRole;

	private Long roleId;

	private String roleName;
	
	private Long partyId;
	
    private String document;
    
    private String docPath;

	private String partyNameEnglish;

	private String partyNameHindi;

	private String partyNameGujarati;

	private Long managerId;

	private String managerFirstname;

	private String managerLastname;

	private Long userId;
	
	private EventMasterResponseDto event;
}
