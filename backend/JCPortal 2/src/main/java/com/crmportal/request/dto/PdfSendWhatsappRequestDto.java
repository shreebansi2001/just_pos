package com.crmportal.request.dto;

import lombok.Data;

@Data
public class PdfSendWhatsappRequestDto {

	private String moduleName;
	private String url;
	private String partyName;
	private String mobileNo;
	private String companyName;
	private String companyMobileNo;
	private Long userId;

}
