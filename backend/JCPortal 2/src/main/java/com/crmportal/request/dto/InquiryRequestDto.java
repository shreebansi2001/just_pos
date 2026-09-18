package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryRequestDto {

	private Long id;
	
	private String inquiryDate;

	private String guestName;

	private String guestAddress;

	private String mobileNo;

	private String tentativeDate;

	private String referralSource;

	private String emailId;
	
	private String function;
	
	private Long userId;
}
