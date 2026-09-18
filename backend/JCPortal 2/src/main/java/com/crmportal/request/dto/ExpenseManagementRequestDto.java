package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.mail.Multipart;

import org.springframework.web.multipart.MultipartFile;

import com.crmportal.enums.EExpense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseManagementRequestDto {

	private Long expenseId;
	
	private String name;
	
	private BigDecimal amount;

	private String date;
	
	private String mobileNo;

	private String paymentType;

	private String remark;
	
	private String description;
	
	private String countryCode;
	
	private String gstin;

	private String buildingAddress;

	private String area;

	private String pincode;

	private String city;

    private String document;
	
	private EExpense userType;

	private String state;

	private Long eventId;
	
	private Long roleId;
	
	private Long partyId;
	
	private Long userId;
	
	private Long managerId;
	
	private MultipartFile file;
}
